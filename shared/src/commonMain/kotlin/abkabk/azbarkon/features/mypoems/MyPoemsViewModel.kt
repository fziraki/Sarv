package abkabk.azbarkon.features.mypoems

import abkabk.azbarkon.core.domain.result.onFailure
import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.core.uidata.BaseViewModel
import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.core.uidata.UiText
import abkabk.azbarkon.core.uidata.toUiText
import abkabk.azbarkon.domain.repository.PoemRepository
import abkabk.azbarkon.domain.repository.SavedPoemRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MyPoemsViewModel(
    private val poemRepository: PoemRepository,
    private val savedPoemRepository: SavedPoemRepository,
) : BaseViewModel<MyPoemsAction, MyPoemsState, MyPoemsEvent>(
        initialState = MyPoemsState(),
    ) {
    init {
        onAction(MyPoemsAction.OnLoad)
    }

    override fun onAction(action: MyPoemsAction) {
        when (action) {
            MyPoemsAction.OnLoad,
            MyPoemsAction.OnResume,
            -> loadMyPoems()

            is MyPoemsAction.OnTabSelected -> {
                setState { copy(selectedTab = action.tab) }
            }

            is MyPoemsAction.OnPoemClick -> {
                viewModelScope.launch {
                    sendEvent(MyPoemsEvent.NavigateToPoemDetail(poemId = action.poemId))
                }
            }

            is MyPoemsAction.OnRemovePoem -> {
                when (state.value.selectedTab) {
                    MyPoemsTab.Liked -> savedPoemRepository.removeLike(action.poemId)
                    MyPoemsTab.Bookmarked -> savedPoemRepository.removeBookmark(action.poemId)
                }
                loadMyPoems()
            }

            MyPoemsAction.OnClearAllClick -> {
                if (!state.value.isActiveTabEmpty) {
                    setState { copy(showClearDialog = true) }
                }
            }

            MyPoemsAction.OnClearAllConfirm -> {
                when (state.value.selectedTab) {
                    MyPoemsTab.Liked -> savedPoemRepository.clearLiked()
                    MyPoemsTab.Bookmarked -> savedPoemRepository.clearBookmarked()
                }
                setState { copy(showClearDialog = false) }
                loadMyPoems()
            }

            MyPoemsAction.OnClearAllDismiss -> {
                setState { copy(showClearDialog = false) }
            }
        }
    }

    private fun loadMyPoems() {
        viewModelScope.launch {
            setState { copy(screenState = UiScreenState.Loading) }

            val likedIds = savedPoemRepository.getLikedIds()
            val bookmarkedIds = savedPoemRepository.getBookmarkedIds()

            coroutineScope {
                val likedDeferred = async { poemRepository.getPoemsByIds(likedIds) }
                val bookmarkedDeferred = async { poemRepository.getPoemsByIds(bookmarkedIds) }

                val likedResult = likedDeferred.await()
                val bookmarkedResult = bookmarkedDeferred.await()

                likedResult
                    .onSuccess { likedPoems ->
                        bookmarkedResult
                            .onSuccess { bookmarkedPoems ->
                                setState {
                                    copy(
                                        screenState = UiScreenState.Success,
                                        likedGroups = likedPoems.toPoetGroups(),
                                        bookmarkedGroups = bookmarkedPoems.toPoetGroups(),
                                    )
                                }
                            }.onFailure { error ->
                                handleError(error.toUiText())
                            }
                    }.onFailure { error ->
                        handleError(error.toUiText())
                    }
            }
        }
    }

    private fun handleError(message: UiText) {
        setState {
            copy(
                screenState = UiScreenState.Error(message = message),
            )
        }
    }
}
