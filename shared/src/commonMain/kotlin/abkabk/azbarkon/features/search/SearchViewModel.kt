package abkabk.azbarkon.features.search

import abkabk.azbarkon.core.domain.result.onFailure
import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.core.ui_base.BaseViewModel
import abkabk.azbarkon.core.ui_base.UiText
import abkabk.azbarkon.data.mapper.collectCatIdsInSubtreeFromTree
import abkabk.azbarkon.domain.model.PoetCategoryNode
import abkabk.azbarkon.domain.repository.PoetRepository
import abkabk.azbarkon.domain.repository.SearchRepository
import androidx.lifecycle.viewModelScope
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.search_empty_query
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository,
    private val poetRepository: PoetRepository,
    private val initialPoetId: Int?,
    private val initialCatId: Int?,
) : BaseViewModel<SearchAction, SearchState, SearchEvent>(
        initialState = SearchState(),
    ) {
    private var categoryTree: List<PoetCategoryNode> = emptyList()
    private var currentOffset = 0
    private var lastSubmittedQuery = ""

    init {
        initialize()
    }

    override fun onAction(action: SearchAction) {
        when (action) {
            is SearchAction.OnQueryChange -> {
                setState { copy(query = action.query) }
            }

            SearchAction.OnSearchSubmit -> submitSearch()

            is SearchAction.OnPoetSelected -> {
                if (action.poetId == state.value.selectedPoetId) return
                setState {
                    copy(
                        selectedPoetId = action.poetId,
                        selectedCategoryId = null,
                    )
                }
                loadCategoriesForSelectedPoet(reloadSearch = lastSubmittedQuery.isNotBlank())
            }

            is SearchAction.OnCategorySelected -> {
                setState { copy(selectedCategoryId = action.categoryId) }
                if (lastSubmittedQuery.isNotBlank()) {
                    loadFirstPage(lastSubmittedQuery)
                }
            }

            is SearchAction.OnResultClick -> {
                viewModelScope.launch {
                    sendEvent(SearchEvent.NavigateToPoemDetail(poemId = action.poemId))
                }
            }

            SearchAction.OnLoadMore -> loadNextPage()
        }
    }

    private fun initialize() {
        viewModelScope.launch {
            poetRepository
                .getPoets()
                .onSuccess { poets ->
                    val poetOptions =
                        buildList {
                            add(SearchPoetOptionUi(id = null, name = ALL_POETS_LABEL))
                            poets.forEach { poet ->
                                val id = poet.id ?: return@forEach
                                val name = poet.name ?: return@forEach
                                add(SearchPoetOptionUi(id = id, name = name))
                            }
                        }

                    val resolvedPoetId = resolveInitialPoetId(poetOptions)
                    setState {
                        copy(
                            poetOptions = poetOptions,
                            selectedPoetId = resolvedPoetId,
                            isInitializing = false,
                        )
                    }
                    loadCategoriesForSelectedPoet(
                        preferredCategoryId = initialCatId,
                        reloadSearch = false,
                    )
                }.onFailure {
                    setState { copy(isInitializing = false) }
                }
        }
    }

    private suspend fun resolveInitialPoetId(poetOptions: List<SearchPoetOptionUi>): Int? {
        initialPoetId?.let { return it }

        if (initialCatId != null) {
            searchRepository
                .getCatById(initialCatId)
                .onSuccess { cat -> return cat.poetId }
        }

        return poetOptions.getOrNull(1)?.id
    }

    private fun loadCategoriesForSelectedPoet(
        preferredCategoryId: Int? = null,
        reloadSearch: Boolean = false,
    ) {
        val poetId = state.value.selectedPoetId
        if (poetId == null) {
            categoryTree = emptyList()
            setState {
                copy(
                    categoryOptions = listOf(SearchCategoryOptionUi(id = null, title = ALL_CATEGORIES_LABEL, depth = 0)),
                    selectedCategoryId = null,
                    isCategoryPickerEnabled = false,
                )
            }
            if (reloadSearch && lastSubmittedQuery.isNotBlank()) {
                loadFirstPage(lastSubmittedQuery)
            }
            return
        }

        viewModelScope.launch {
            poetRepository
                .getPoetWithCategories(poetId)
                .onSuccess { poetWithCategories ->
                    categoryTree = poetWithCategories.categories
                    val categoryOptions =
                        buildList {
                            add(SearchCategoryOptionUi(id = null, title = ALL_CATEGORIES_LABEL, depth = 0))
                            addAll(flattenAllSearchCategories(poetWithCategories.categories))
                        }
                    val selectedCategoryId =
                        preferredCategoryId?.takeIf { id ->
                            categoryOptions.any { option -> option.id == id }
                        }
                    setState {
                        copy(
                            categoryOptions = categoryOptions,
                            selectedCategoryId = selectedCategoryId,
                            isCategoryPickerEnabled = true,
                        )
                    }
                    if (reloadSearch && lastSubmittedQuery.isNotBlank()) {
                        loadFirstPage(lastSubmittedQuery)
                    }
                }
        }
    }

    private fun submitSearch() {
        val query = state.value.query.trim()
        if (query.isEmpty()) {
            viewModelScope.launch {
                sendEvent(
                    SearchEvent.ShowSnackbar(
                        UiText.Resource(Res.string.search_empty_query),
                    ),
                )
            }
            return
        }
        loadFirstPage(query)
    }

    private fun loadFirstPage(query: String) {
        lastSubmittedQuery = query
        currentOffset = 0
        viewModelScope.launch {
            setState {
                copy(
                    isSearching = true,
                    isLoadingMore = false,
                    showNoResults = false,
                )
            }

            searchRepository
                .searchVerses(
                    query = query,
                    poetId = state.value.selectedPoetId,
                    categoryIds = resolveCategoryFilterIds(),
                    offset = 0,
                    limit = PAGE_SIZE,
                ).onSuccess { page ->
                    currentOffset = page.hits.size
                    setState {
                        copy(
                            isSearching = false,
                            submittedQuery = query,
                            results = page.hits.map { it.toSearchResultUi() },
                            hasMore = currentOffset < page.totalCount,
                            showNoResults = page.hits.isEmpty(),
                        )
                    }
                }.onFailure {
                    setState { copy(isSearching = false) }
                }
        }
    }

    private fun loadNextPage() {
        val currentState = state.value
        if (currentState.isLoadingMore || currentState.isSearching || !currentState.hasMore) return

        viewModelScope.launch {
            setState { copy(isLoadingMore = true) }

            searchRepository
                .searchVerses(
                    query = lastSubmittedQuery,
                    poetId = currentState.selectedPoetId,
                    categoryIds = resolveCategoryFilterIds(),
                    offset = currentOffset,
                    limit = PAGE_SIZE,
                ).onSuccess { page ->
                    currentOffset += page.hits.size
                    setState {
                        copy(
                            isLoadingMore = false,
                            results = results + page.hits.map { it.toSearchResultUi() },
                            hasMore = currentOffset < page.totalCount,
                        )
                    }
                }.onFailure {
                    setState { copy(isLoadingMore = false) }
                }
        }
    }

    private fun resolveCategoryFilterIds(): Set<Int>? {
        val categoryId = state.value.selectedCategoryId ?: return null
        return collectCatIdsInSubtreeFromTree(categoryId, categoryTree)
    }

    private companion object {
        const val PAGE_SIZE = 20
        const val ALL_POETS_LABEL = "همه"
        const val ALL_CATEGORIES_LABEL = "همه"
    }
}
