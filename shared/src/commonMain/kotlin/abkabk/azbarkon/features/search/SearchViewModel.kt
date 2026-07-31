package abkabk.azbarkon.features.search

import abkabk.azbarkon.core.domain.result.onFailure
import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.core.uidata.BaseViewModel
import abkabk.azbarkon.core.uidata.UiText
import abkabk.azbarkon.data.mapper.collectCatIdsInSubtreeFromTree
import abkabk.azbarkon.domain.model.PoetCategoryNode
import abkabk.azbarkon.domain.repository.PoetRepository
import abkabk.azbarkon.domain.repository.SearchRepository
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map as pagingMap
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.search_empty_query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
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
    private val searchParams = MutableStateFlow<SearchParams?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: Flow<PagingData<SearchResultUi>> =
        searchParams
            .flatMapLatest { params ->
                if (params == null || params.query.isBlank()) {
                    flowOf(PagingData.empty())
                } else {
                    searchRepository
                        .searchVerses(
                            query = params.query,
                            poetId = params.poetId,
                            categoryIds = params.categoryIds,
                        ).map { pagingData -> pagingData.pagingMap { it.toSearchResultUi() } }
                }
            }.cachedIn(viewModelScope)

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
                loadCategoriesForSelectedPoet(reloadSearch = state.value.submittedQuery.isNotBlank())
            }

            is SearchAction.OnCategorySelected -> {
                setState { copy(selectedCategoryId = action.categoryId) }
                refreshSearchIfActive()
            }

            is SearchAction.OnResultClick -> {
                viewModelScope.launch {
                    sendEvent(SearchEvent.NavigateToPoemDetail(poemId = action.poemId))
                }
            }
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
            if (reloadSearch) {
                refreshSearchIfActive()
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
                    if (reloadSearch) {
                        refreshSearchIfActive()
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
        setState { copy(submittedQuery = query) }
        updateSearchParams(query)
    }

    private fun refreshSearchIfActive() {
        val query = state.value.submittedQuery
        if (query.isNotBlank()) {
            updateSearchParams(query)
        }
    }

    private fun updateSearchParams(query: String) {
        searchParams.value =
            SearchParams(
                query = query,
                poetId = state.value.selectedPoetId,
                categoryIds = resolveCategoryFilterIds(),
            )
    }

    private fun resolveCategoryFilterIds(): Set<Int>? {
        val categoryId = state.value.selectedCategoryId ?: return null
        return collectCatIdsInSubtreeFromTree(categoryId, categoryTree)
    }

    private companion object {
        const val ALL_POETS_LABEL = "همه"
        const val ALL_CATEGORIES_LABEL = "همه"
    }
}
