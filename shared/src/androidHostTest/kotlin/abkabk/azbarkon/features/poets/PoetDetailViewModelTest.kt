package abkabk.azbarkon.features.poets

import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.PoetCategoryNode
import abkabk.azbarkon.domain.model.PoetWithCategories
import abkabk.azbarkon.testing.FakePoetRepository
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
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
class PoetDetailViewModelTest {
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
    fun `loads poet detail by id`() =
        runTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithCategories =
                        listOf(
                            PoetWithCategories(
                                poet =
                                    Poet(
                                        id = 2,
                                        name = "حافظ شیرازی",
                                        description = "بیو",
                                        rootCatId = 9,
                                        imageUrl = "https://example.com/hafez.png",
                                    ),
                                categories =
                                    listOf(
                                        PoetCategoryNode(
                                            id = 24,
                                            text = "غزلیات",
                                            url = "/ghazals",
                                            children =
                                                listOf(
                                                    PoetCategoryNode(
                                                        id = 100,
                                                        text = "غزل ۱",
                                                        url = "/ghazals/1",
                                                    ),
                                                ),
                                        ),
                                        PoetCategoryNode(
                                            id = 25,
                                            text = "قطعات",
                                            url = "/qataat",
                                        ),
                                    ),
                            ),
                        )
                }
            val viewModel = PoetDetailViewModel(repository, poetId = 2)

            val state = viewModel.state.value
            assertThat(state.screenState).isInstanceOf(UiScreenState.Success::class)
            assertThat(state.name).isEqualTo("حافظ شیرازی")
            assertThat(state.bio).isEqualTo("بیو")
            assertThat(state.categories).hasSize(2)
            assertThat(state.categories.first().title).isEqualTo("غزلیات")
        }

    @Test
    fun `toggle expands parent category children`() =
        runTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithCategories =
                        listOf(
                            PoetWithCategories(
                                poet =
                                    Poet(
                                        id = 2,
                                        name = "حافظ",
                                        description = null,
                                        rootCatId = 9,
                                        imageUrl = null,
                                    ),
                                categories =
                                    listOf(
                                        PoetCategoryNode(
                                            id = 24,
                                            text = "غزلیات",
                                            url = "/ghazals",
                                            children =
                                                listOf(
                                                    PoetCategoryNode(
                                                        id = 100,
                                                        text = "غزل ۱",
                                                        url = "/ghazals/1",
                                                    ),
                                                ),
                                        ),
                                    ),
                            ),
                        )
                }
            val viewModel = PoetDetailViewModel(repository, poetId = 2)

            viewModel.onAction(PoetDetailAction.OnCategoryToggle(categoryId = 24))

            val state = viewModel.state.value
            assertThat(state.categories).hasSize(2)
            assertThat(state.categories.last().title).isEqualTo("غزل ۱")
            assertThat(state.categories.last().depth).isEqualTo(1)
        }

    @Test
    fun `leaf click emits navigate to poem list event`() =
        runTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithCategories =
                        listOf(
                            PoetWithCategories(
                                poet =
                                    Poet(
                                        id = 2,
                                        name = "حافظ",
                                        description = null,
                                        rootCatId = 9,
                                        imageUrl = null,
                                    ),
                                categories =
                                    listOf(
                                        PoetCategoryNode(
                                            id = 25,
                                            text = "قطعات",
                                            url = "/qataat",
                                        ),
                                    ),
                            ),
                        )
                }
            val viewModel = PoetDetailViewModel(repository, poetId = 2)

            viewModel.events.test {
                viewModel.onAction(
                    PoetDetailAction.OnCategoryClick(
                        categoryId = 25,
                        title = "قطعات",
                    ),
                )

                assertThat(awaitItem()).isEqualTo(
                    PoetDetailEvent.NavigateToPoemList(
                        catId = 25,
                        title = "قطعات",
                    ),
                )
            }
        }
}
