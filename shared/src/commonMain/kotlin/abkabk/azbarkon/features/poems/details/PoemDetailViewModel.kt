package abkabk.azbarkon.features.poems.details

import abkabk.azbarkon.core.domain.result.onFailure
import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.core.ui_base.BaseViewModel
import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.UiText
import abkabk.azbarkon.core.ui_base.toUiText
import abkabk.azbarkon.domain.platform.ShareService
import abkabk.azbarkon.domain.repository.PoemRepository
import abkabk.azbarkon.domain.repository.SavedPoemRepository
import androidx.lifecycle.viewModelScope
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.coming_soon
import azbarkoncmp.shared.generated.resources.search_empty_query
import azbarkoncmp.shared.generated.resources.search_not_found_in_poem
import kotlinx.coroutines.launch

class PoemDetailViewModel(
    private val poemRepository: PoemRepository,
    private val savedPoemRepository: SavedPoemRepository,
    private val shareService: ShareService,
    private val poemId: Int,
) : BaseViewModel<PoemDetailAction, PoemDetailState, PoemDetailEvent>(
        initialState = PoemDetailState(),
    ) {
    init {
        onAction(PoemDetailAction.OnLoad)
    }

    override fun onAction(action: PoemDetailAction) {
        when (action) {
            PoemDetailAction.OnLoad,
            PoemDetailAction.OnRetryClick,
            -> loadPoemDetail()

            PoemDetailAction.OnSearchClick -> toggleFindBar()

            is PoemDetailAction.OnFindQueryChange -> {
                setState { copy(findInput = action.query) }
            }

            PoemDetailAction.OnFindSubmit -> applyFindQuery(state.value.findInput)

            PoemDetailAction.OnFindBarClose -> closeFindBar()

            PoemDetailAction.OnScrollConsumed -> {
                setState { copy(scrollToVerseId = null) }
            }

            PoemDetailAction.OnShareClick -> sharePoem()

            PoemDetailAction.OnLikeClick -> toggleLike()

            PoemDetailAction.OnBookmarkClick -> toggleBookmark()

            PoemDetailAction.OnImageCreatorClick -> navigateToTasvirNegar()

            PoemDetailAction.OnMemorizeClick,
            -> showComingSoon()
        }
    }

    private fun loadPoemDetail() {
        viewModelScope.launch {
            setState { copy(screenState = UiScreenState.Loading) }

            poemRepository
                .getPoemDetail(poemId)
                .onSuccess { detail ->
                    setState {
                        copy(
                            screenState = UiScreenState.Success,
                            poetName = detail.poetName,
                            subtitle = detail.title,
                            verses = detail.verses.map { it.toPoemVerseUi() },
                            isLiked = savedPoemRepository.isLiked(poemId),
                            isBookmarked = savedPoemRepository.isBookmarked(poemId),
                        )
                    }
                }.onFailure { error ->
                    val message = error.toUiText()
                    setState {
                        copy(screenState = UiScreenState.Error(message = message))
                    }
                    sendEvent(PoemDetailEvent.ShowSnackbar(message))
                }
        }
    }

    private fun toggleFindBar() {
        val currentState = state.value
        if (currentState.isFindBarVisible) {
            closeFindBar()
            return
        }

        val prefilledInput =
            currentState.findInput.ifBlank {
                currentState.highlightQuery
            }

        setState {
            copy(
                isFindBarVisible = true,
                findInput = prefilledInput,
            )
        }
    }

    private fun closeFindBar() {
        setState {
            copy(
                isFindBarVisible = false,
                findInput = "",
                highlightQuery = "",
                scrollToVerseId = null,
            )
        }
    }

    private fun applyFindQuery(query: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) {
            viewModelScope.launch {
                sendEvent(
                    PoemDetailEvent.ShowSnackbar(
                        UiText.Resource(Res.string.search_empty_query),
                    ),
                )
            }
            return
        }

        val matchingVerse = findFirstMatchingVerse(state.value.verses, trimmedQuery)
        if (matchingVerse == null) {
            viewModelScope.launch {
                sendEvent(
                    PoemDetailEvent.ShowSnackbar(
                        UiText.Resource(Res.string.search_not_found_in_poem),
                    ),
                )
            }
            return
        }

        setState {
            copy(
                highlightQuery = trimmedQuery,
                findInput = trimmedQuery,
                scrollToVerseId = matchingVerse.id,
            )
        }
    }

    private fun sharePoem() {
        val text = buildShareText()
        if (text.isBlank()) return

        shareService.shareText(
            text = text,
            title = state.value.subtitle.ifBlank { state.value.poetName },
        )
    }

    private fun toggleLike() {
        val isLiked = savedPoemRepository.toggleLike(poemId)
        setState { copy(isLiked = isLiked) }
    }

    private fun toggleBookmark() {
        val isBookmarked = savedPoemRepository.toggleBookmark(poemId)
        setState { copy(isBookmarked = isBookmarked) }
    }

    private fun navigateToTasvirNegar() {
        viewModelScope.launch {
            sendEvent(PoemDetailEvent.NavigateToTasvirNegar)
        }
    }

    private fun showComingSoon() {
        viewModelScope.launch {
            sendEvent(
                PoemDetailEvent.ShowSnackbar(
                    UiText.Resource(Res.string.coming_soon),
                ),
            )
        }
    }

    private fun buildShareText(): String {
        val currentState = state.value
        if (currentState.verses.isEmpty()) return ""

        return buildString {
            if (currentState.poetName.isNotBlank()) {
                append(currentState.poetName)
            }
            if (currentState.subtitle.isNotBlank()) {
                if (isNotEmpty()) append("\n")
                append(currentState.subtitle)
            }
            if (isNotEmpty()) append("\n\n")
            append(currentState.verses.joinToString("\n") { it.text })
        }
    }
}
