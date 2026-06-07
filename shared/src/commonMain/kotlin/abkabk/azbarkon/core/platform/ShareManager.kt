@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package abkabk.azbarkon.core.platform

expect class ShareManager {
    fun shareText(
        text: String,
        title: String?,
    )
}
