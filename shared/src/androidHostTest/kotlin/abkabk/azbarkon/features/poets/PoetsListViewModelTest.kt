package abkabk.azbarkon.features.poets

import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.PoetWithWorks
import abkabk.azbarkon.domain.model.PoetWork
import abkabk.azbarkon.testing.FakePoetRepository
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import kotlin.random.Random
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
class PoetsListViewModelTest {
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
    fun `search by poet name filters list`() =
        runTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithWorks = samplePoetsWithWorks()
                }
            val viewModel = PoetsListViewModel(repository)

            viewModel.onAction(PoetsListAction.OnSearchQueryChange("سعد"))

            val state = viewModel.state.value
            assertThat(state.screenState).isInstanceOf(UiScreenState.Success::class)
            assertThat(state.poets).hasSize(1)
            assertThat(state.poets.first().name).isEqualTo("سعدی شیرازی")
            assertThat(state.featuredPoet).isNull()
        }

    @Test
    fun `search by work title filters list`() =
        runTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithWorks = samplePoetsWithWorks()
                }
            val viewModel = PoetsListViewModel(repository)

            viewModel.onAction(PoetsListAction.OnSearchQueryChange("گلستان"))

            val state = viewModel.state.value
            assertThat(state.poets).hasSize(1)
            assertThat(state.poets.first().name).isEqualTo("سعدی شیرازی")
        }

    @Test
    fun `poet click emits navigation event`() =
        runTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithWorks = samplePoetsWithWorks()
                }
            val viewModel = PoetsListViewModel(repository)

            viewModel.events.test {
                viewModel.onAction(PoetsListAction.OnPoetClick(7))
                val event = awaitItem()
                assertThat(event).isInstanceOf(PoetsListEvent.NavigateToPoetDetail::class)
                assertThat((event as PoetsListEvent.NavigateToPoetDetail).poetId).isEqualTo(7)
            }
        }

    @Test
    fun `initial load picks featured poet from list using random`() =
        runTest {
            val poets = samplePoetsWithWorks()
            val repository =
                FakePoetRepository().apply {
                    poetsWithWorks = poets
                }
            val seed = randomSeedForIndex(size = poets.size, desiredIndex = 1)
            val viewModel = PoetsListViewModel(repository, random = Random(seed))

            val featured = viewModel.state.value.featuredPoet
            assertThat(featured).isNotNull()
            assertThat(featured!!.id).isEqualTo(poets[1].poet.id)
        }

    @Test
    fun `on screen enter re-picks featured poet from loaded list`() =
        runTest {
            val poets = samplePoetsWithWorks()
            val repository =
                FakePoetRepository().apply {
                    poetsWithWorks = poets
                }
            val seed = randomSeedForConsecutiveIndices(size = poets.size, firstIndex = 1, secondIndex = 0)
            val viewModel = PoetsListViewModel(repository, random = Random(seed))
            val initialFeatured = viewModel.state.value.featuredPoet

            viewModel.onAction(PoetsListAction.OnScreenEnter)

            val featured = viewModel.state.value.featuredPoet
            assertThat(featured).isNotNull()
            assertThat(featured!!.id).isEqualTo(poets[0].poet.id)
            assertThat(featured.id).isNotEqualTo(initialFeatured!!.id)
        }

    @Test
    fun `clearing search restores featured poet without re-randomizing`() =
        runTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithWorks = samplePoetsWithWorks()
                }
            val seed = randomSeedForIndex(size = samplePoetsWithWorks().size, desiredIndex = 1)
            val viewModel = PoetsListViewModel(repository, random = Random(seed))
            val featuredBeforeSearch = viewModel.state.value.featuredPoet
            assertThat(featuredBeforeSearch).isNotNull()

            viewModel.onAction(PoetsListAction.OnSearchQueryChange("سعد"))
            assertThat(viewModel.state.value.featuredPoet).isNull()

            viewModel.onAction(PoetsListAction.OnSearchQueryChange(""))
            assertThat(viewModel.state.value.featuredPoet).isEqualTo(featuredBeforeSearch)
        }

    @Test
    fun `on screen enter with empty list leaves featured poet null`() =
        runTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithWorks = emptyList()
                }
            val viewModel = PoetsListViewModel(repository)

            viewModel.onAction(PoetsListAction.OnScreenEnter)

            assertThat(viewModel.state.value.featuredPoet).isNull()
        }

    private fun samplePoetsWithWorks(): List<PoetWithWorks> =
        listOf(
            PoetWithWorks(
                poet =
                    Poet(
                        id = 2,
                        name = "حافظ شیرازی",
                        description = null,
                        rootCatId = 9,
                        imageUrl = null,
                    ),
                works =
                    listOf(
                        PoetWork(id = 9, title = "دیوان حافظ"),
                    ),
            ),
            PoetWithWorks(
                poet =
                    Poet(
                        id = 7,
                        name = "سعدی شیرازی",
                        description = null,
                        rootCatId = 118,
                        imageUrl = null,
                    ),
                works =
                    listOf(
                        PoetWork(id = 1665, title = "گلستان"),
                        PoetWork(id = 123, title = "بوستان"),
                    ),
            ),
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
