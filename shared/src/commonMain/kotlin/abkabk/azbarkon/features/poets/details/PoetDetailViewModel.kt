package abkabk.azbarkon.features.poets.details

import abkabk.azbarkon.core.domain.result.onFailure
import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.core.uidata.BaseViewModel
import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.core.uidata.toUiText
import abkabk.azbarkon.domain.model.PoetCategoryNode
import abkabk.azbarkon.domain.model.hasCategory
import abkabk.azbarkon.domain.repository.PoetRepository
import abkabk.azbarkon.features.poets.GHAZAL_CATEGORY
import abkabk.azbarkon.features.poets.flattenPoetCategories
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PoetDetailViewModel(
    private val poetRepository: PoetRepository,
    private val poetId: Int,
) : BaseViewModel<PoetDetailAction, PoetDetailState, PoetDetailEvent>(
        initialState = PoetDetailState(),
    ) {
    private var categoryTree: List<PoetCategoryNode> = emptyList()
    private var expandedCategoryIds: Set<Int> = emptySet()

    init {
        onAction(PoetDetailAction.OnLoad)
    }

    override fun onAction(action: PoetDetailAction) {
        when (action) {
            PoetDetailAction.OnLoad,
            PoetDetailAction.OnRetryClick,
            -> loadPoet()

            is PoetDetailAction.OnCategoryToggle -> toggleCategory(action.categoryId)

            is PoetDetailAction.OnCategoryClick -> {
                viewModelScope.launch {
                    sendEvent(
                        PoetDetailEvent.NavigateToPoemList(
                            catId = action.categoryId,
                            title = action.title,
                        ),
                    )
                }
            }

            PoetDetailAction.OnChatClick -> {
                viewModelScope.launch {
                    sendEvent(PoetDetailEvent.NavigateToChat(poetId))
                }
            }
        }
    }

    private fun loadPoet() {
        viewModelScope.launch {
            setState { copy(screenState = UiScreenState.Loading) }

            poetRepository.getPoetWithCategories(poetId)
                .onSuccess { poetWithCategories ->
                    categoryTree = poetWithCategories.categories
                    expandedCategoryIds = emptySet()
                    setState {
                        copy(
                            screenState = UiScreenState.Success,
                            name = poetWithCategories.poet.name.orEmpty(),
                            bio = poetWithCategories.poet.description.orEmpty(),
                            imageUrl = poetWithCategories.poet.imageUrl,
                            canChat = poetWithCategories.categories.any { it.hasCategory(GHAZAL_CATEGORY) },
                            categories =
                                flattenPoetCategories(
                                    nodes = categoryTree,
                                    expandedCategoryIds = expandedCategoryIds,
                                ),
                        )
                    }
                }.onFailure { error ->
                    val message = error.toUiText()
                    setState {
                        copy(
                            screenState = UiScreenState.Error(message = message),
                        )
                    }
                    sendEvent(PoetDetailEvent.ShowSnackbar(message))
                }
        }
    }

    private fun toggleCategory(categoryId: Int) {
        expandedCategoryIds =
            if (categoryId in expandedCategoryIds) {
                expandedCategoryIds - categoryId
            } else {
                expandedCategoryIds + categoryId
            }
        setState {
            copy(
                categories =
                    flattenPoetCategories(
                        nodes = categoryTree,
                        expandedCategoryIds = expandedCategoryIds,
                    ),
            )
        }
    }
}
