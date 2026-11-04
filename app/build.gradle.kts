import java.security.MessageDigest

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.alifhasnain.unbundle"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.alifhasnain.unbundle"
        // The bundled aapt2 is built for API 30.
        minSdk = 30
        targetSdk = 36
        // The release workflow passes these from the git tag; local builds fall back to the defaults.
        versionCode = providers.gradleProperty("versionCode").orNull?.toInt() ?: 1
        versionName = providers.gradleProperty("versionName").orNull ?: "1.0.0"
    }

    // Release keystore comes from the environment (CI decodes it from secrets); nothing secret lives in the repo.
    val releaseStore = providers.environmentVariable("SIGNING_STORE_FILE").orNull
    signingConfigs {
        if (releaseStore != null) {
            create("release") {
                storeFile = file(releaseStore)
                storePassword = providers.environmentVariable("SIGNING_STORE_PASSWORD").get()
                keyAlias = providers.environmentVariable("SIGNING_KEY_ALIAS").get()
                keyPassword = providers.environmentVariable("SIGNING_KEY_PASSWORD").get()
            }
        }
    }

    buildTypes {
        release {
            // shortcut: no R8, bundletool reflects into protobuf and keep rules are untested; add them if APK size matters.
            isMinifyEnabled = false
            // Without a release keystore (local builds) the debug key signs, so assembleRelease still installs.
            signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    packaging {
        jniLibs {
            // aapt2 is run as a program, so it must be extracted to nativeLibraryDir and left unstripped.
            useLegacyPackaging = true
            keepDebugSymbols += "**/libaapt2.so"
        }
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE*",
                "META-INF/NOTICE*",
                "META-INF/INDEX.LIST",
                "META-INF/*.kotlin_module",
            )
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

// ReVanced aapt2 v1.1.0, aapt2-arm64-v8a: https://github.com/ReVanced/aapt2/releases/tag/v1.1.0
val aapt2Sha256 = "7e5ae2e1f62fc24cab14072555ffd0a1a7e1ce27e82cc1008967b835b6d8df5b"
val verifyAapt2 by tasks.registering {
    val binary = file("src/main/jniLibs/arm64-v8a/libaapt2.so")
    inputs.file(binary)
    doLast {
        val actual = MessageDigest.getInstance("SHA-256").digest(binary.readBytes()).joinToString("") { "%02x".format(it) }
        check(actual == aapt2Sha256) { "libaapt2.so checksum mismatch: $actual" }
    }
}
tasks.named("preBuild") { dependsOn(verifyAapt2) }

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.bundletool)
    implementation(libs.guava)
}
