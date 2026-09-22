package abkabk.azbarkon.features.memorization.select

import abkabk.azbarkon.domain.model.memorization.MemorizationSummary
import abkabk.azbarkon.testing.FakeMemorizationRepository
import abkabk.azbarkon.testing.runViewModelTest
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import kotlin.test.Test

class MemorizationSelectViewModelTest {

    @Test
    fun `active poem count updates from repository summary`() =
        runViewModelTest {
            val fake = FakeMemorizationRepository()
            val vm = MemorizationSelectViewModel(fake)

            fake.emitSummary(MemorizationSummary(activePoemCount = 3, dueCardsToday = 5))

            assertThat(vm.state.value.activePoemCount).isEqualTo(3)
        }

    @Test
    fun `back click emits NavigateBack`() =
        runViewModelTest {
            val vm = MemorizationSelectViewModel(FakeMemorizationRepository())

            vm.events.test {
                vm.onAction(MemorizationSelectAction.OnBackClick)
                assertThat(awaitItem()).isEqualTo(MemorizationSelectEvent.NavigateBack)
            }
        }

    @Test
    fun `treasury click emits NavigateToTreasury`() =
        runViewModelTest {
            val vm = MemorizationSelectViewModel(FakeMemorizationRepository())

            vm.events.test {
                vm.onAction(MemorizationSelectAction.OnTreasuryClick)
                assertThat(awaitItem()).isEqualTo(MemorizationSelectEvent.NavigateToTreasury)
            }
        }

    @Test
    fun `search click emits NavigateToSearch`() =
        runViewModelTest {
            val vm = MemorizationSelectViewModel(FakeMemorizationRepository())

            vm.events.test {
                vm.onAction(MemorizationSelectAction.OnSearchClick)
                assertThat(awaitItem()).isEqualTo(MemorizationSelectEvent.NavigateToSearch)
            }
        }

    @Test
    fun `active poems click emits NavigateToActivePoems`() =
        runViewModelTest {
            val vm = MemorizationSelectViewModel(FakeMemorizationRepository())

            vm.events.test {
                vm.onAction(MemorizationSelectAction.OnActivePoemsClick)
                assertThat(awaitItem()).isEqualTo(MemorizationSelectEvent.NavigateToActivePoems)
            }
        }

    @Test
    fun `hafez ghazals click with catId navigates to poem list`() =
        runViewModelTest {
            val fake = FakeMemorizationRepository()
            val vm = MemorizationSelectViewModel(fake)

            vm.events.test {
                vm.onAction(MemorizationSelectAction.OnHafezGhazalsClick)
                assertThat(awaitItem()).isEqualTo(MemorizationSelectEvent.NavigateToTreasury)
            }
        }

    @Test
    fun `hafez ghazals click with poetId navigates to poet detail`() =
        runViewModelTest {
            val fake = FakeMemorizationRepository()
            val vm = MemorizationSelectViewModel(fake)

            vm.events.test {
                vm.onAction(MemorizationSelectAction.OnHafezGhazalsClick)
                assertThat(awaitItem()).isEqualTo(MemorizationSelectEvent.NavigateToTreasury)
            }
        }

    @Test
    fun `baba taher couplets click navigates`() =
        runViewModelTest {
            val vm = MemorizationSelectViewModel(FakeMemorizationRepository())

            vm.events.test {
                vm.onAction(MemorizationSelectAction.OnBabaTaherCoupletsClick)
                assertThat(awaitItem()).isEqualTo(MemorizationSelectEvent.NavigateToTreasury)
            }
        }

    @Test
    fun `khayyam rubaiyat click navigates`() =
        runViewModelTest {
            val vm = MemorizationSelectViewModel(FakeMemorizationRepository())

            vm.events.test {
                vm.onAction(MemorizationSelectAction.OnKhayyamRubaiyatClick)
                assertThat(awaitItem()).isEqualTo(MemorizationSelectEvent.NavigateToTreasury)
            }
        }
}
