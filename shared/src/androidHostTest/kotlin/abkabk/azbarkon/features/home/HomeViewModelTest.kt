package abkabk.azbarkon.features.home

import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.usecase.GetPoetsLocallyUseCase
import abkabk.azbarkon.testing.FakePoetRepository
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loading local poets updates success state`() =
        runTest {
            val repository =
                FakePoetRepository().apply {
                    localPoets =
                        listOf(
                            Poet(
                                id = 1,
                                name = "حافظ",
                                description = null,
                                rootCatId = null,
                                imageUrl = null,
                            ),
                        )
                }
            val viewModel = HomeViewModel(GetPoetsLocallyUseCase(repository))

            val state = viewModel.state.value
            assertThat(state.screenState).isInstanceOf(UiScreenState.Success::class)
            assertThat(state.poets.size).isEqualTo(1)
        }

    @Test
    fun `local load failure emits snackbar event`() =
        runTest {
            val repository =
                FakePoetRepository().apply {
                    shouldFailLocal = true
                }
            val viewModel = HomeViewModel(GetPoetsLocallyUseCase(repository))

            viewModel.events.test {
                val event = awaitItem()
                assertThat(event).isInstanceOf(HomeEvent.ShowSnackbar::class)
            }
        }
}
