package abkabk.azbarkon.core.ui_base

import org.jetbrains.compose.resources.StringResource

sealed interface UiText {
    data class DynamicString(
        val value: String,
    ) : UiText

    data class Resource(
        val resource: StringResource,
    ) : UiText
}
