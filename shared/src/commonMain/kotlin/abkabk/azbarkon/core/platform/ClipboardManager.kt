@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package abkabk.azbarkon.core.platform

expect class ClipboardManager {
    fun copyToClipboard(text: String)
}
