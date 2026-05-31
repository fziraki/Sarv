package abkabk.azbarkon.features.home

import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.testing.FakePoetRepository
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
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
    fun `loading poets updates success state`() =
        runTest {
            val repository =
                FakePoetRepository().apply {
                    poets =
                        listOf(
                            Poet(
                                id = 1,
                                name = "حافظ",
                                description = null,
                                rootCatId = 10,
                                imageUrl = "https://api.ganjoor.net/api/ganjoor/poet/image/hafez.png",
                            ),
                        )
                }
            val viewModel = HomeViewModel(repository)

            val state = viewModel.state.value
            assertThat(state.screenState).isInstanceOf(UiScreenState.Success::class)
            assertThat(state.poets.size).isEqualTo(1)
            assertThat(state.poets.first().imageUrl).isNotNull()
        }

    @Test
    fun `load failure emits snackbar event`() =
        runTest {
            val repository =
                FakePoetRepository().apply {
                    shouldFail = true
                }
            val viewModel = HomeViewModel(repository)

            viewModel.events.test {
                val event = awaitItem()
                assertThat(event).isInstanceOf(HomeEvent.ShowSnackbar::class)
            }
        }
}
