package abkabk.azbarkon.core.local

expect class PoetDbFileStorage {
    fun downloadDir(): String

    fun writeBytes(fileName: String, bytes: ByteArray)

    fun delete(fileName: String)
}
