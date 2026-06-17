package abkabk.azbarkon.features.search

import abkabk.azbarkon.core.ui_base.UiText
import abkabk.azbarkon.domain.model.CatNode
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.PoetCategoryNode
import abkabk.azbarkon.domain.model.PoetWithCategories
import abkabk.azbarkon.domain.model.SearchHit
import abkabk.azbarkon.testing.FakePoetRepository
import abkabk.azbarkon.testing.FakeSearchRepository
import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.search_empty_query
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
class SearchViewModelTest {
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
    fun `preselects poet and category from cat id`() =
        runTest {
            val poetRepository =
                FakePoetRepository().apply {
                    poets = listOf(samplePoet(id = 2))
                    poetsWithCategories =
                        listOf(
                            PoetWithCategories(
                                poet = samplePoet(id = 2),
                                categories = sampleCategoryTree(),
                            ),
                        )
                }
            val searchRepository =
                FakeSearchRepository().apply {
                    catsById =
                        mapOf(
                            24 to
                                CatNode(
                                    id = 24,
                                    poetId = 2,
                                    text = "غزلیات",
                                    parentId = 9,
                                    url = "/ghazals",
                                ),
                        )
                }

            val viewModel =
                SearchViewModel(
                    searchRepository = searchRepository,
                    poetRepository = poetRepository,
                    initialPoetId = null,
                    initialCatId = 24,
                )

            val state = viewModel.state.value
            assertThat(state.selectedPoetId).isEqualTo(2)
            assertThat(state.selectedCategoryId).isEqualTo(24)
            assertThat(state.isCategoryPickerEnabled).isTrue()
        }

    @Test
    fun `changing poet resets category selection`() =
        runTest {
            val poetRepository =
                FakePoetRepository().apply {
                    poets = listOf(samplePoet(id = 2), samplePoet(id = 3, name = "سعدی"))
                    poetsWithCategories =
                        listOf(
                            PoetWithCategories(
                                poet = samplePoet(id = 2),
                                categories = sampleCategoryTree(),
                            ),
                            PoetWithCategories(
                                poet = samplePoet(id = 3, name = "سعدی"),
                                categories = emptyList(),
                            ),
                        )
                }
            val viewModel =
                SearchViewModel(
                    searchRepository = FakeSearchRepository(),
                    poetRepository = poetRepository,
                    initialPoetId = 2,
                    initialCatId = 24,
                )

            viewModel.onAction(SearchAction.OnPoetSelected(3))

            val state = viewModel.state.value
            assertThat(state.selectedPoetId).isEqualTo(3)
            assertThat(state.selectedCategoryId).isEqualTo(null)
        }

    @Test
    fun `empty query shows snackbar`() =
        runTest {
            val viewModel =
                SearchViewModel(
                    searchRepository = FakeSearchRepository(),
                    poetRepository = FakePoetRepository().apply { poets = listOf(samplePoet(id = 2)) },
                    initialPoetId = null,
                    initialCatId = null,
                )

            viewModel.events.test {
                viewModel.onAction(SearchAction.OnSearchSubmit)

                assertThat(awaitItem()).isEqualTo(
                    SearchEvent.ShowSnackbar(UiText.Resource(Res.string.search_empty_query)),
                )
            }
        }

    @Test
    fun `successful search populates results`() =
        runTest {
            val searchRepository =
                FakeSearchRepository().apply {
                    searchPages =
                        listOf(
                            SearchHit(
                                poemId = 1,
                                poemTitle = "غزل ۱",
                                poetName = "حافظ",
                                categoryName = "غزلیات",
                                verseText = "بیت",
                                verseOrder = 1,
                            ),
                        )
                }
            val viewModel =
                SearchViewModel(
                    searchRepository = searchRepository,
                    poetRepository = FakePoetRepository().apply { poets = listOf(samplePoet(id = 2)) },
                    initialPoetId = null,
                    initialCatId = null,
                )

            viewModel.onAction(SearchAction.OnQueryChange("بیت"))
            viewModel.onAction(SearchAction.OnSearchSubmit)

            val results = viewModel.searchResults.asSnapshot()
            assertThat(results).hasSize(1)
            assertThat(viewModel.state.value.submittedQuery).isEqualTo("بیت")
        }

    @Test
    fun `result click navigates to poem detail`() =
        runTest {
            val viewModel =
                SearchViewModel(
                    searchRepository = FakeSearchRepository(),
                    poetRepository = FakePoetRepository().apply { poets = listOf(samplePoet(id = 2)) },
                    initialPoetId = null,
                    initialCatId = null,
                )

            viewModel.events.test {
                viewModel.onAction(SearchAction.OnResultClick(42))

                assertThat(awaitItem()).isEqualTo(
                    SearchEvent.NavigateToPoemDetail(poemId = 42),
                )
            }
        }

    @Test
    fun `pagination loads all pages`() =
        runTest {
            val searchRepository =
                FakeSearchRepository().apply {
                    searchPages =
                        (1..25).map { index ->
                            SearchHit(
                                poemId = index,
                                poemTitle = "غزل $index",
                                poetName = "حافظ",
                                categoryName = "غزلیات",
                                verseText = "بیت $index",
                                verseOrder = 1,
                            )
                        }
                }
            val viewModel =
                SearchViewModel(
                    searchRepository = searchRepository,
                    poetRepository = FakePoetRepository().apply { poets = listOf(samplePoet(id = 2)) },
                    initialPoetId = null,
                    initialCatId = null,
                )

            viewModel.onAction(SearchAction.OnQueryChange("بیت"))
            viewModel.onAction(SearchAction.OnSearchSubmit)

            val results =
                viewModel.searchResults.asSnapshot {
                    scrollTo(index = 24)
                }
            assertThat(results).hasSize(25)
        }

    private fun samplePoet(
        id: Int,
        name: String = "حافظ",
    ): Poet =
        Poet(
            id = id,
            name = name,
            description = null,
            rootCatId = 9,
            imageUrl = null,
        )

    private fun sampleCategoryTree(): List<PoetCategoryNode> =
        listOf(
            PoetCategoryNode(
                id = 24,
                text = "غزلیات",
                url = "/ghazals",
                children = emptyList(),
            ),
        )
}
