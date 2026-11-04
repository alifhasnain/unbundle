package com.alifhasnain.unbundle

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.core.graphics.drawable.toBitmap
import com.android.tools.build.bundletool.androidtools.Aapt2Command
import com.android.tools.build.bundletool.commands.BuildApksCommand
import com.android.tools.build.bundletool.model.SigningConfiguration
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Proxy
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest
import java.security.cert.X509Certificate
import java.util.zip.ZipFile
import javax.security.auth.x500.X500Principal

enum class Step { Copy, Read, Convert, Package }

class ApkInfo(
    val file: File,
    val label: String,
    val icon: Bitmap,
    val packageName: String,
    val versionName: String?,
    val versionCode: Long,
    val minSdk: Int,
    val targetSdk: Int,
    val sizeBytes: Long,
    val sha256: String,
)

/** Turns an .aab into a signed universal APK with bundletool and the bundled aapt2. Blocking; call off the main thread. */
class Converter(private val context: Context) {
    private val work = File(context.cacheDir, "work")
    private val out = File(context.cacheDir, "out") // shared through FileProvider, see res/xml/file_paths.xml

    fun convert(input: Uri, onStep: (Step) -> Unit): ApkInfo {
        val aapt2 = File(context.applicationInfo.nativeLibraryDir, "libaapt2.so")
        check(aapt2.exists()) {
            "This phone's CPU (${Build.SUPPORTED_ABIS.first()}) is not supported. Unbundle ships aapt2 for arm64-v8a only."
        }
        work.deleteRecursively()
        out.deleteRecursively()
        work.mkdirs()
        out.mkdirs()

        onStep(Step.Copy)
        val aab = File(work, "input.aab")
        val source = context.contentResolver.openInputStream(input) ?: error("Could not open the selected file.")
        source.use { i -> aab.outputStream().use { i.copyTo(it) } }

        onStep(Step.Read)
        // Name the common wrong picks in plain words before bundletool's own, more cryptic error.
        val zip = runCatching { ZipFile(aab) }.getOrNull() ?: error(NOT_A_BUNDLE)
        zip.use {
            if (it.getEntry("BundleConfig.pb") == null) {
                error(if (it.getEntry("AndroidManifest.xml") != null) "This is an APK, not an App Bundle. Pick the .aab file instead." else NOT_A_BUNDLE)
            }
        }

        // bundletool gives no progress callbacks; its aapt2 calls mark where Convert starts and Package begins.
        val real = Aapt2Command.createFromExecutablePath(aapt2.toPath())
        val observed = Proxy.newProxyInstance(
            Aapt2Command::class.java.classLoader,
            arrayOf(Aapt2Command::class.java),
        ) { _, method, args ->
            val isAapt2Call = method.declaringClass == Aapt2Command::class.java
            if (isAapt2Call) onStep(Step.Convert)
            val result = try {
                method.invoke(real, *(args ?: emptyArray()))
            } catch (e: InvocationTargetException) {
                throw e.targetException
            }
            if (isAapt2Call) onStep(Step.Package)
            result
        } as Aapt2Command

        val apks = File(work, "apks")
        BuildApksCommand.builder()
            .setBundlePath(aab.toPath())
            .setOutputFile(apks.toPath())
            .setOutputFormat(BuildApksCommand.OutputFormat.DIRECTORY)
            .setApkBuildMode(BuildApksCommand.ApkBuildMode.UNIVERSAL)
            .setAapt2Command(observed)
            .setSigningConfiguration(signingConfiguration())
            .build()
            .execute()

        val apk = File(out, "universal.apk")
        val built = apks.walk().firstOrNull { it.extension == "apk" } ?: error("bundletool produced no APK.")
        built.copyTo(apk, overwrite = true)
        work.deleteRecursively()
        return inspect(apk)
    }

    private fun inspect(apk: File): ApkInfo {
        val pm = context.packageManager
        val pkg = (if (Build.VERSION.SDK_INT >= 33) {
            pm.getPackageArchiveInfo(apk.path, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION") pm.getPackageArchiveInfo(apk.path, 0)
        }) ?: error("Android could not read the converted APK.")
        val app = requireNotNull(pkg.applicationInfo).apply {
            sourceDir = apk.path
            publicSourceDir = apk.path
        }
        return ApkInfo(
            file = apk,
            label = app.loadLabel(pm).toString(),
            icon = app.loadIcon(pm).toBitmap(144, 144),
            packageName = pkg.packageName,
            versionName = pkg.versionName,
            versionCode = pkg.longVersionCode,
            minSdk = app.minSdkVersion,
            targetSdk = app.targetSdkVersion,
            sizeBytes = apk.length(),
            sha256 = sha256(apk),
        )
    }

    /** A per-device RSA key that never leaves the Android Keystore. Created on first use. */
    private fun signingConfiguration(): SigningConfiguration {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (!store.containsAlias(KEY_ALIAS)) {
            KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore").run {
                initialize(
                    KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_SIGN)
                        .setKeySize(2048)
                        // apksig signs v1 with SHA-1 for minSdk < 18 and SHA-256 above; v2/v3 may use SHA-512.
                        .setDigests(KeyProperties.DIGEST_SHA1, KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                        .setCertificateSubject(X500Principal("CN=Unbundle on-device key"))
                        .build(),
                )
                generateKeyPair()
            }
        }
        val entry = store.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
        return SigningConfiguration.builder()
            .setSignerConfig(entry.privateKey, entry.certificate as X509Certificate)
            .build()
    }

    private companion object {
        const val KEY_ALIAS = "unbundle-signing"
        const val NOT_A_BUNDLE = "This file is not an Android App Bundle (.aab)."
    }
}

fun sha256(file: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    file.inputStream().use { input ->
        val buffer = ByteArray(1 shl 16)
        while (true) {
            val read = input.read(buffer)
            if (read < 0) break
            digest.update(buffer, 0, read)
        }
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}
