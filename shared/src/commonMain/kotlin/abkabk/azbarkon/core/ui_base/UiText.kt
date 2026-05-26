package abkabk.azbarkon.core.ui_base

sealed interface UiText {

    data class Dynamic(
        val value: String
    ) : UiText

    data class StringResource(
        val resId: String
    ) : UiText
}

fun UiText.asString(): String {
    return when (this) {

        is UiText.Dynamic -> value

        is UiText.StringResource -> resId

    }
}