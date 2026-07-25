package abkabk.azbarkon.features.home

import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.domain.model.CatNode
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.PoetWithRootCategories
import abkabk.azbarkon.testing.FakeDailyBeytRepository
import abkabk.azbarkon.testing.FakeMemorizationRepository
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
                    poetsWithRootCategories =
                        listOf(
                            PoetWithRootCategories(
                                poet =
                                    Poet(
                                        id = 1,
                                        name = "حافظ",
                                        description = null,
                                        rootCatId = 10,
                                        imageUrl = "https://api.ganjoor.net/api/ganjoor/poet/image/hafez.png",
                                    ),
                                rootCategories =
                                    listOf(
                                        CatNode(
                                            id = 100,
                                            poetId = 1,
                                            text = "غزلیات",
                                            parentId = 10,
                                            url = "/test",
                                        ),
                                    ),
                            ),
                        )
                }
            val viewModel = HomeViewModel(repository, FakeMemorizationRepository(), FakeDailyBeytRepository())

            val state = viewModel.state.value
            assertThat(state.screenState).isInstanceOf(UiScreenState.Success::class)
            assertThat(state.poets.size).isEqualTo(1)
            assertThat(state.poets.first().imageUrl).isNotNull()
        }

    @Test
    fun `poet with no root categories is excluded from home list`() =
        runTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithRootCategories =
                        listOf(
                            PoetWithRootCategories(
                                poet =
                                    Poet(
                                        id = 1,
                                        name = "حافظ",
                                        description = null,
                                        rootCatId = 10,
                                        imageUrl = null,
                                    ),
                                rootCategories =
                                    listOf(
                                        CatNode(
                                            id = 100,
                                            poetId = 1,
                                            text = "غزلیات",
                                            parentId = 10,
                                            url = "/test",
                                        ),
                                    ),
                            ),
                            PoetWithRootCategories(
                                poet =
                                    Poet(
                                        id = 99,
                                        name = "شاعر بدون اثر",
                                        description = null,
                                        rootCatId = 500,
                                        imageUrl = null,
                                    ),
                                rootCategories = emptyList(),
                            ),
                        )
                }
            val viewModel = HomeViewModel(repository, FakeMemorizationRepository(), FakeDailyBeytRepository())

            val state = viewModel.state.value
            assertThat(state.poets.size).isEqualTo(1)
            assertThat(state.poets.first().id).isEqualTo(1)
        }

    @Test
    fun `load failure emits snackbar event`() =
        runTest {
            val repository =
                FakePoetRepository().apply {
                    shouldFail = true
                }
            val viewModel = HomeViewModel(repository, FakeMemorizationRepository(), FakeDailyBeytRepository())

            viewModel.events.test {
                val event = awaitItem()
                assertThat(event).isInstanceOf(HomeEvent.ShowSnackbar::class)
            }
        }
}
