package abkabk.azbarkon.features.poems.list

import abkabk.azbarkon.core.uidata.BaseViewModel
import abkabk.azbarkon.domain.repository.PoemRepository
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map as pagingMap
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PoemListViewModel(
    private val poemRepository: PoemRepository,
    private val catId: Int,
    title: String,
) : BaseViewModel<PoemListAction, PoemListState, PoemListEvent>(
        initialState = PoemListState(title = title),
    ) {
    val poems: Flow<PagingData<PoemListItemUi>> =
        poemRepository
            .poemsByCatId(catId)
            .map { pagingData ->
                pagingData.pagingMap { poem ->
                    PoemListItemUi(
                        id = poem.id,
                        title = poem.title,
                    )
                }
            }.cachedIn(viewModelScope)

    override fun onAction(action: PoemListAction) {
        when (action) {
            is PoemListAction.OnPoemClick -> {
                Napier.d(message = "clicked poemId=${action.poemId}", tag = "PoemDebug")
                viewModelScope.launch {
                    sendEvent(PoemListEvent.NavigateToPoemDetail(poemId = action.poemId))
                }
            }
        }
    }
}
