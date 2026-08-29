package abkabk.azbarkon.features.poets

import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.domain.model.CatNode
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.PoetWithRootCategories
import abkabk.azbarkon.features.poets.list.PoetsListAction
import abkabk.azbarkon.features.poets.list.PoetsListEvent
import abkabk.azbarkon.features.poets.list.PoetsListViewModel
import abkabk.azbarkon.testing.FakePoetDownloadRepository
import abkabk.azbarkon.testing.FakePoetRepository
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isTrue
import kotlin.random.Random
import abkabk.azbarkon.testing.runViewModelTest
import kotlin.test.Test

class PoetsListViewModelTest {

    @Test
    fun `search by poet name filters list`() =
        runViewModelTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithRootCategories = samplePoetsWithRootCategories()
                }
            val viewModel = PoetsListViewModel(repository, FakePoetDownloadRepository())

            viewModel.onAction(PoetsListAction.OnSearchQueryChange("سعد"))

            val state = viewModel.state.value
            assertThat(state.screenState).isInstanceOf(UiScreenState.Success::class)
            assertThat(state.poets).hasSize(1)
            assertThat(state.poets.first().name).isEqualTo("سعدی شیرازی")
            assertThat(state.featuredPoet).isNull()
        }

    @Test
    fun `search by category name filters list`() =
        runViewModelTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithRootCategories = samplePoetsWithRootCategories()
                }
            val viewModel = PoetsListViewModel(repository, FakePoetDownloadRepository())

            viewModel.onAction(PoetsListAction.OnSearchQueryChange("غزلیات"))

            val state = viewModel.state.value
            assertThat(state.poets).hasSize(1)
            assertThat(state.poets.first().name).isEqualTo("حافظ شیرازی")
        }

    @Test
    fun `maps root categories to summary text`() =
        runViewModelTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithRootCategories = samplePoetsWithRootCategories()
                }
            val viewModel = PoetsListViewModel(repository, FakePoetDownloadRepository())

            val state = viewModel.state.value
            assertThat(state.poets.first { it.name == "حافظ شیرازی" }.worksSummary)
                .isEqualTo("قطعات و 4 اثر دیگر")
            assertThat(state.poets.first { it.name == "سعدی شیرازی" }.worksSummary)
                .isEqualTo("گلستان و 1 اثر دیگر")
        }

    @Test
    fun `chat is available for poets with ghazal category at any depth`() =
        runViewModelTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithRootCategories =
                        samplePoetsWithRootCategories() +
                            PoetWithRootCategories(
                                poet =
                                    Poet(
                                        id = 8,
                                        name = "شاعر بدون غزل",
                                        description = null,
                                        rootCatId = 300,
                                        imageUrl = null,
                                    ),
                                rootCategories =
                                    listOf(
                                        cat(id = 301, poetId = 8, text = "قصاید", parentId = 300),
                                    ),
                                allCategories =
                                    listOf(
                                        cat(id = 301, poetId = 8, text = "قصاید", parentId = 300),
                                    ),
                            )
                }
            val viewModel = PoetsListViewModel(repository, FakePoetDownloadRepository())

            val state = viewModel.state.value
            assertThat(state.poets.first { it.name == "حافظ شیرازی" }.canChat).isTrue()
            assertThat(state.poets.first { it.name == "سعدی شیرازی" }.canChat).isTrue()
            assertThat(state.poets.first { it.name == "شاعر بدون غزل" }.canChat).isFalse()
        }

    @Test
    fun `poet click emits navigation event`() =
        runViewModelTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithRootCategories = samplePoetsWithRootCategories()
                }
            val viewModel = PoetsListViewModel(repository, FakePoetDownloadRepository())

            viewModel.events.test {
                viewModel.onAction(PoetsListAction.OnPoetClick(7))
                val event = awaitItem()
                assertThat(event).isInstanceOf(PoetsListEvent.NavigateToPoetDetail::class)
                assertThat((event as PoetsListEvent.NavigateToPoetDetail).poetId).isEqualTo(7)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `initial load picks featured poet from list using random`() =
        runViewModelTest {
            val poets = samplePoetsWithRootCategories()
            val repository =
                FakePoetRepository().apply {
                    poetsWithRootCategories = poets
                }
            val seed = randomSeedForIndex(size = poets.size, desiredIndex = 1)
            val viewModel = PoetsListViewModel(repository, FakePoetDownloadRepository(), random = Random(seed))

            val featured = viewModel.state.value.featuredPoet
            assertThat(featured).isNotNull()
            assertThat(featured!!.id).isEqualTo(poets[1].poet.id)
        }

    @Test
    fun `on screen enter re-picks featured poet from loaded list`() =
        runViewModelTest {
            val poets = samplePoetsWithRootCategories()
            val repository =
                FakePoetRepository().apply {
                    poetsWithRootCategories = poets
                }
            val seed = randomSeedForConsecutiveIndices(size = poets.size, firstIndex = 1, secondIndex = 0)
            val viewModel = PoetsListViewModel(repository, FakePoetDownloadRepository(), random = Random(seed))
            val initialFeatured = viewModel.state.value.featuredPoet

            viewModel.onAction(PoetsListAction.OnScreenEnter)

            val featured = viewModel.state.value.featuredPoet
            assertThat(featured).isNotNull()
            assertThat(featured!!.id).isEqualTo(poets[0].poet.id)
            assertThat(featured.id).isNotEqualTo(initialFeatured!!.id)
        }

    @Test
    fun `clearing search restores featured poet without re-randomizing`() =
        runViewModelTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithRootCategories = samplePoetsWithRootCategories()
                }
            val seed = randomSeedForIndex(size = samplePoetsWithRootCategories().size, desiredIndex = 1)
            val viewModel = PoetsListViewModel(repository, FakePoetDownloadRepository(), random = Random(seed))
            val featuredBeforeSearch = viewModel.state.value.featuredPoet
            assertThat(featuredBeforeSearch).isNotNull()

            viewModel.onAction(PoetsListAction.OnSearchQueryChange("سعد"))
            assertThat(viewModel.state.value.featuredPoet).isNull()

            viewModel.onAction(PoetsListAction.OnSearchQueryChange(""))
            assertThat(viewModel.state.value.featuredPoet).isEqualTo(featuredBeforeSearch)
        }

    @Test
    fun `on screen enter with empty list leaves featured poet null`() =
        runViewModelTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithRootCategories = emptyList()
                }
            val viewModel = PoetsListViewModel(repository, FakePoetDownloadRepository())

            viewModel.onAction(PoetsListAction.OnScreenEnter)

            assertThat(viewModel.state.value.featuredPoet).isNull()
        }

    @Test
    fun `poet with no root categories is excluded from list`() =
        runViewModelTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithRootCategories =
                        samplePoetsWithRootCategories() +
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
                            )
                }
            val viewModel = PoetsListViewModel(repository, FakePoetDownloadRepository())

            val state = viewModel.state.value
            assertThat(state.poets).hasSize(2)
            assertThat(state.poets.any { it.id == 99 }).isFalse()
        }

    private fun samplePoetsWithRootCategories(): List<PoetWithRootCategories> =
        listOf(
            PoetWithRootCategories(
                poet =
                    Poet(
                        id = 2,
                        name = "حافظ شیرازی",
                        description = null,
                        rootCatId = 9,
                        imageUrl = null,
                    ),
                rootCategories =
                    listOf(
                        cat(id = 24, poetId = 2, text = "غزلیات", parentId = 9),
                        cat(id = 25, poetId = 2, text = "قطعات", parentId = 9),
                        cat(id = 26, poetId = 2, text = "رباعیات", parentId = 9),
                        cat(id = 27, poetId = 2, text = "قصاید", parentId = 9),
                        cat(id = 28, poetId = 2, text = "اشعار منتسب", parentId = 9),
                    ),
                allCategories =
                    listOf(
                        cat(id = 0, poetId = 2, text = "حافظ", parentId = 9),
                        cat(id = 24, poetId = 2, text = "غزلیات", parentId = 9),
                        cat(id = 25, poetId = 2, text = "قطعات", parentId = 9),
                        cat(id = 26, poetId = 2, text = "رباعیات", parentId = 9),
                        cat(id = 27, poetId = 2, text = "قصاید", parentId = 9),
                        cat(id = 28, poetId = 2, text = "اشعار منتسب", parentId = 9),
                    ),
            ),
            PoetWithRootCategories(
                poet =
                    Poet(
                        id = 7,
                        name = "سعدی شیرازی",
                        description = null,
                        rootCatId = 118,
                        imageUrl = null,
                    ),
                rootCategories =
                    listOf(
                        cat(id = 1665, poetId = 7, text = "گلستان", parentId = 118),
                        cat(id = 123, poetId = 7, text = "بوستان", parentId = 118),
                    ),
                allCategories =
                    listOf(
                        cat(id = 1665, poetId = 7, text = "گلستان", parentId = 118),
                        cat(id = 123, poetId = 7, text = "بوستان", parentId = 118),
                        cat(id = 122, poetId = 7, text = "دیوان اشعار", parentId = 118),
                        cat(id = 124, poetId = 7, text = "غزلیات", parentId = 122),
                    ),
            ),
        )

    private fun cat(
        id: Int,
        poetId: Int,
        text: String,
        parentId: Int,
    ): CatNode =
        CatNode(
            id = id,
            poetId = poetId,
            text = text,
            parentId = parentId,
            url = "/test",
        )

    private fun randomSeedForIndex(
        size: Int,
        desiredIndex: Int,
    ): Int =
        generateSequence(0) { it + 1 }.first { seed ->
            Random(seed).nextInt(size) == desiredIndex
        }

    private fun randomSeedForConsecutiveIndices(
        size: Int,
        firstIndex: Int,
        secondIndex: Int,
    ): Int =
        generateSequence(0) { it + 1 }.first { seed ->
            val random = Random(seed)
            random.nextInt(size) == firstIndex && random.nextInt(size) == secondIndex
        }
}
