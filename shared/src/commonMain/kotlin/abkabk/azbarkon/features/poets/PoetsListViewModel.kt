package abkabk.azbarkon.features.poets

import abkabk.azbarkon.core.domain.result.onFailure
import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.core.ui_base.BaseViewModel
import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.toUiText
import abkabk.azbarkon.domain.model.PoetWithWorks
import abkabk.azbarkon.domain.repository.PoetRepository
import androidx.lifecycle.viewModelScope
import kotlin.random.Random
import kotlinx.coroutines.launch

class PoetsListViewModel(
    private val poetRepository: PoetRepository,
    private val random: Random = Random.Default,
) : BaseViewModel<PoetsListAction, PoetsListState, PoetsListEvent>(
        initialState = PoetsListState(),
    ) {
    private var allPoetsWithWorks: List<PoetWithWorks> = emptyList()
    private var featuredPoetWhenIdle: FeaturedPoetUi? = null

    init {
        onAction(PoetsListAction.OnLoad)
    }

    override fun onAction(action: PoetsListAction) {
        when (action) {
            PoetsListAction.OnLoad,
            PoetsListAction.OnRetryClick,
            -> loadPoets()

            PoetsListAction.OnScreenEnter -> pickAndApplyFeaturedPoet()

            is PoetsListAction.OnSearchQueryChange -> updateSearch(action.query)

            is PoetsListAction.OnPoetClick -> {
                viewModelScope.launch {
                    sendEvent(PoetsListEvent.NavigateToPoetDetail(action.poetId))
                }
            }

            PoetsListAction.OnFeaturedPoetClick -> {
                val featuredId = state.value.featuredPoet?.id ?: return
                viewModelScope.launch {
                    sendEvent(PoetsListEvent.NavigateToPoetDetail(featuredId))
                }
            }
        }
    }

    private fun loadPoets() {
        viewModelScope.launch {
            setState { copy(screenState = UiScreenState.Loading) }

            poetRepository.getPoetsWithWorks()
                .onSuccess { poetsWithWorks ->
                    allPoetsWithWorks = poetsWithWorks
                    val featured = pickRandomFeatured(poetsWithWorks)
                    applyFilter(
                        query = state.value.searchQuery,
                        source = poetsWithWorks,
                        featuredPoet = featured,
                    )
                }.onFailure { error ->
                    val message = error.toUiText()
                    setState {
                        copy(
                            screenState = UiScreenState.Error(message = message),
                        )
                    }
                    sendEvent(PoetsListEvent.ShowSnackbar(message))
                }
        }
    }

    private fun pickAndApplyFeaturedPoet() {
        if (allPoetsWithWorks.isEmpty()) return
        if (state.value.searchQuery.trim().isNotEmpty()) return

        val featured = pickRandomFeatured(allPoetsWithWorks)
        applyFilter(
            query = state.value.searchQuery,
            source = allPoetsWithWorks,
            featuredPoet = featured,
        )
    }

    private fun updateSearch(query: String) {
        setState { copy(searchQuery = query) }
        applyFilter(
            query = query,
            source = allPoetsWithWorks,
        )
    }

    private fun applyFilter(
        query: String,
        source: List<PoetWithWorks>,
        featuredPoet: FeaturedPoetUi? = null,
    ) {
        val trimmedQuery = query.trim()
        val filtered =
            if (trimmedQuery.isEmpty()) {
                source
            } else {
                source.filter { item ->
                    item.poet.name?.contains(trimmedQuery) == true ||
                        item.works.any { work -> work.title.contains(trimmedQuery) }
                }
            }

        val listItems = filtered.map { it.toListItemUi() }
        val resolvedFeatured =
            when {
                trimmedQuery.isNotEmpty() -> null
                featuredPoet != null -> featuredPoet.also { featuredPoetWhenIdle = it }
                else -> featuredPoetWhenIdle
            }

        setState {
            copy(
                screenState = UiScreenState.Success,
                poets = listItems,
                featuredPoet = resolvedFeatured,
            )
        }
    }

    private fun pickRandomFeatured(source: List<PoetWithWorks>): FeaturedPoetUi? =
        source.randomOrNull(random)?.toFeaturedPoetUi()
}
