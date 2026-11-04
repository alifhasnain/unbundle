package com.alifhasnain.unbundle

import android.app.Application
import android.net.Uri
import android.os.SystemClock
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class BundleFile(val name: String, val sizeBytes: Long?)

/** Elapsed-realtime marks; 0 means not reached. */
data class StepTime(val start: Long = 0L, val end: Long = 0L)

data class UiState(
    val bundle: BundleFile? = null,
    val departedAt: Long = 0L,
    val steps: List<StepTime> = List(Step.entries.size) { StepTime() },
    val apk: ApkInfo? = null,
    val error: String? = null,
) {
    val working get() = bundle != null && apk == null && error == null
}

class UnbundleViewModel(app: Application) : AndroidViewModel(app) {
    private val converter = Converter(app)
    private val _state = MutableStateFlow(UiState())
    val state = _state.asStateFlow()

    fun convert(uri: Uri) {
        if (_state.value.working) return
        _state.value = UiState(bundle = describe(uri), departedAt = System.currentTimeMillis())
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apk = converter.convert(uri, ::markStep)
                val now = SystemClock.elapsedRealtime()
                _state.update { s ->
                    s.copy(apk = apk, steps = s.steps.map { StepTime(if (it.start > 0) it.start else now, if (it.end > 0) it.end else now) })
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                // bundletool wraps the useful message (bad bundle, aapt2 output) a few causes deep.
                val reason = generateSequence(e) { it.cause }.mapNotNull { it.message }.lastOrNull() ?: e.javaClass.simpleName
                _state.update { it.copy(error = reason) }
            }
        }
    }

    /** Copies the converted APK to a document the user picked. Returns an error message, or null on success. */
    suspend fun save(target: Uri): String? = withContext(Dispatchers.IO) {
        val apk = _state.value.apk ?: return@withContext "Nothing to save yet."
        runCatching {
            val sink = getApplication<Application>().contentResolver.openOutputStream(target) ?: error("Could not open the destination.")
            sink.use { s -> apk.file.inputStream().use { it.copyTo(s) } }
        }.exceptionOrNull()?.let { it.message ?: "Could not save the APK." }
    }

    private fun markStep(step: Step) {
        val now = SystemClock.elapsedRealtime()
        _state.update { s ->
            s.copy(steps = s.steps.mapIndexed { i, t ->
                when {
                    i < step.ordinal && t.end == 0L -> StepTime(if (t.start == 0L) now else t.start, now)
                    i == step.ordinal && t.start == 0L -> t.copy(start = now)
                    else -> t
                }
            })
        }
    }

    private fun describe(uri: Uri): BundleFile {
        val resolver = getApplication<Application>().contentResolver
        resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)?.use { c ->
            if (c.moveToFirst()) {
                val name = c.getString(0) ?: uri.lastPathSegment ?: "bundle.aab"
                val size = if (c.isNull(1)) null else c.getLong(1)
                return BundleFile(name, size)
            }
        }
        return BundleFile(uri.lastPathSegment ?: "bundle.aab", null)
    }
}
