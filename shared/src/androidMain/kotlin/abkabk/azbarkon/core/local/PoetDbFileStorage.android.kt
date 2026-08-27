package abkabk.azbarkon.core.local

import android.content.Context
import java.io.File

actual class PoetDbFileStorage(
    context: Context,
) {
    private val dir = File(context.filesDir, "poet_downloads").apply { mkdirs() }

    actual fun downloadDir(): String = dir.absolutePath

    actual fun writeBytes(fileName: String, bytes: ByteArray) {
        File(dir, fileName).writeBytes(bytes)
    }

    actual fun delete(fileName: String) {
        File(dir, fileName).delete()
    }
}
