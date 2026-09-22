package abkabk.azbarkon.features.memorization.list

import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.domain.model.memorization.MemorizationError
import abkabk.azbarkon.domain.model.memorization.MemorizationPoem
import abkabk.azbarkon.domain.model.memorization.MemorizationStatus
import abkabk.azbarkon.testing.FakeMemorizationRepository
import abkabk.azbarkon.testing.runViewModelTest
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import kotlin.test.Test

class MemorizationListViewModelTest {

    @Test
    fun `initial load sets success with poems`() =
        runViewModelTest {
            val fake = FakeMemorizationRepository().apply {
                activePoems = Result.Success(
                    listOf(
                        memorizationPoem(poemId = 1, title = "Ghazal 1"),
                        memorizationPoem(poemId = 2, title = "Ghazal 2"),
                    ),
                )
            }
            val vm = MemorizationListViewModel(fake)

            val state = vm.state.value
            assertThat(state.screenState).isEqualTo(UiScreenState.Success)
            assertThat(state.poems.size).isEqualTo(2)
            assertThat(state.poems.first().poemId).isEqualTo(1)
        }

    @Test
    fun `initial load sets error on failure`() =
        runViewModelTest {
            val fake = FakeMemorizationRepository().apply {
                activePoems = Result.Error(MemorizationError.Unknown)
            }
            val vm = MemorizationListViewModel(fake)

            assertThat(vm.state.value.screenState).isInstanceOf(UiScreenState.Error::class)
        }

    @Test
    fun `delete click sets poemToDelete`() =
        runViewModelTest {
            val fake = FakeMemorizationRepository().apply {
                activePoems = Result.Success(listOf(memorizationPoem(poemId = 5)))
            }
            val vm = MemorizationListViewModel(fake)

            vm.onAction(MemorizationAction.OnDeleteClick(poemId = 5))

            assertThat(vm.state.value.poemToDelete).isEqualTo(5)
        }

    @Test
    fun `delete dismiss clears poemToDelete`() =
        runViewModelTest {
            val fake = FakeMemorizationRepository().apply {
                activePoems = Result.Success(listOf(memorizationPoem(poemId = 5)))
            }
            val vm = MemorizationListViewModel(fake)

            vm.onAction(MemorizationAction.OnDeleteClick(poemId = 5))
            vm.onAction(MemorizationAction.OnDeleteDismiss)

            assertThat(vm.state.value.poemToDelete).isNull()
        }

    @Test
    fun `delete confirm removes poem and reloads`() =
        runViewModelTest {
            val fake = FakeMemorizationRepository().apply {
                activePoems = Result.Success(listOf(memorizationPoem(poemId = 5)))
            }
            val vm = MemorizationListViewModel(fake)

            vm.onAction(MemorizationAction.OnDeleteClick(poemId = 5))
            vm.onAction(MemorizationAction.OnDeleteConfirm)

            assertThat(vm.state.value.poemToDelete).isNull()
        }

    @Test
    fun `tab selection updates state`() =
        runViewModelTest {
            val fake = FakeMemorizationRepository()
            val vm = MemorizationListViewModel(fake)

            vm.onAction(MemorizationAction.OnTabSelected(MemorizationTab.COMPLETED))

            assertThat(vm.state.value.selectedTab).isEqualTo(MemorizationTab.COMPLETED)
        }

    @Test
    fun `back click emits NavigateBack event`() =
        runViewModelTest {
            val vm = MemorizationListViewModel(FakeMemorizationRepository())

            vm.events.test {
                vm.onAction(MemorizationAction.OnBackClick)
                assertThat(awaitItem()).isEqualTo(MemorizationEvent.NavigateBack)
            }
        }

    @Test
    fun `add poem click emits NavigateToSelect event`() =
        runViewModelTest {
            val vm = MemorizationListViewModel(FakeMemorizationRepository())

            vm.events.test {
                vm.onAction(MemorizationAction.OnAddPoemClick)
                assertThat(awaitItem()).isEqualTo(MemorizationEvent.NavigateToSelect)
            }
        }

    @Test
    fun `poem click emits NavigateToPractice event`() =
        runViewModelTest {
            val vm = MemorizationListViewModel(FakeMemorizationRepository())

            vm.events.test {
                vm.onAction(MemorizationAction.OnPoemClick(poemId = 42))
                assertThat(awaitItem()).isEqualTo(MemorizationEvent.NavigateToPractice(poemId = 42))
            }
        }

    @Test
    fun `re-review resets poem and reloads`() =
        runViewModelTest {
            val fake = FakeMemorizationRepository().apply {
                activePoems = Result.Success(listOf(memorizationPoem(poemId = 3)))
            }
            val vm = MemorizationListViewModel(fake)

            vm.onAction(MemorizationAction.OnReReviewClick(poemId = 3))

            assertThat(vm.state.value.screenState).isEqualTo(UiScreenState.Success)
        }
}

private fun memorizationPoem(
    poemId: Int = 1,
    title: String = "Test Poem",
    poetName: String = "Hafez",
    status: MemorizationStatus = MemorizationStatus.ACTIVE,
    reviewSessionsCount: Int = 0,
    nextReviewDays: Int = 0,
    totalCards: Int = 10,
    reviewedCards: Int = 0,
) = MemorizationPoem(
    poemId = poemId,
    title = title,
    poetName = poetName,
    categoryName = "غزلیات",
    addedAtMillis = 0L,
    status = status,
    totalCards = totalCards,
    reviewedCards = reviewedCards,
    reviewSessionsCount = reviewSessionsCount,
    nextReviewDays = nextReviewDays,
    dueDate = 0L,
)
