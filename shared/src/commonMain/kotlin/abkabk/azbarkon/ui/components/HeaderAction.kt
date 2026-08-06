package abkabk.azbarkon.ui.components

sealed interface HeaderAction {
    val onClick: () -> Unit

    data class Search(override val onClick: () -> Unit) : HeaderAction
    data class Bookmark(val isBookmarked: Boolean, override val onClick: () -> Unit) : HeaderAction
    data class ClearAll(override val onClick: () -> Unit) : HeaderAction
    data class Alarm(val isEnabled: Boolean, override val onClick: () -> Unit) : HeaderAction
}
