package abkabk.azbarkon.features.poets

import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.PoetWithWorks
import abkabk.azbarkon.domain.model.PoetWork
import abkabk.azbarkon.testing.FakePoetRepository
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
                    poetsWithWorks =
                        listOf(
                            PoetWithWorks(
                                poet =
                                    Poet(
                                        id = 2,
                                        name = "حافظ شیرازی",
                                        description = "بیو",
                                        rootCatId = 9,
                                        imageUrl = "https://example.com/hafez.png",
                                    ),
                                works =
                                    listOf(
                                        PoetWork(id = 9, title = "دیوان حافظ", subtitle = "غزلیات"),
                                    ),
                            ),
                        )
                }
            val viewModel = PoetDetailViewModel(repository, poetId = 2)

            val state = viewModel.state.value
            assertThat(state.screenState).isInstanceOf(UiScreenState.Success::class)
            assertThat(state.name).isEqualTo("حافظ شیرازی")
            assertThat(state.bio).isEqualTo("بیو")
            assertThat(state.works).hasSize(1)
            assertThat(state.works.first().title).isEqualTo("دیوان حافظ")
        }
}
