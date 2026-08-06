package abkabk.azbarkon.features.search

import abkabk.azbarkon.core.uidata.UiText
import androidx.compose.runtime.Stable

@Stable
data class SearchState(
    val query: String = "",
    val submittedQuery: String = "",
    val poetOptions: List<SearchPoetOptionUi> = emptyList(),
    val categoryOptions: List<SearchCategoryOptionUi> = emptyList(),
    val selectedPoetId: Int? = null,
    val selectedCategoryId: Int? = null,
    val isCategoryPickerEnabled: Boolean = false,
    val isInitializing: Boolean = true,
)

sealed interface SearchAction {
    data class OnQueryChange(
        val query: String,
    ) : SearchAction

    data object OnSearchSubmit : SearchAction

    data class OnPoetSelected(
        val poetId: Int?,
    ) : SearchAction

    data class OnCategorySelected(
        val categoryId: Int?,
    ) : SearchAction

    data class OnResultClick(
        val poemId: Int,
    ) : SearchAction
}

sealed interface SearchEvent {
    data class NavigateToPoemDetail(
        val poemId: Int,
    ) : SearchEvent

    data class ShowSnackbar(
        val message: UiText,
    ) : SearchEvent
}

internal data class SearchParams(
    val query: String,
    val poetId: Int?,
    val categoryIds: Set<Int>?,
)
