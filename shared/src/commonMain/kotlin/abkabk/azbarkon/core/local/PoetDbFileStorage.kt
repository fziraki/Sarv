@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package abkabk.azbarkon.core.local

expect class PoetDbFileStorage {
    fun downloadDir(): String

    fun writeBytes(fileName: String, bytes: ByteArray)

    fun delete(fileName: String)
}
