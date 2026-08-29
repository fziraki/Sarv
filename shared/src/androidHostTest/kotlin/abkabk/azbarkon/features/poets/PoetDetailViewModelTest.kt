package abkabk.azbarkon.features.poets

import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.PoetCategoryNode
import abkabk.azbarkon.domain.model.PoetWithCategories
import abkabk.azbarkon.features.poets.details.PoetDetailAction
import abkabk.azbarkon.features.poets.details.PoetDetailEvent
import abkabk.azbarkon.features.poets.details.PoetDetailViewModel
import abkabk.azbarkon.domain.usecase.GetRandomGhazalForPoetUseCase
import abkabk.azbarkon.testing.FakePoetRepository
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.sarv.db.SarvDatabase
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class PoetDetailViewModelTest {

    private val getRandomGhazalForPoet: GetRandomGhazalForPoetUseCase by lazy {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        listOf(
            "CREATE TABLE poet (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL, cat_id INTEGER NOT NULL, description TEXT NOT NULL)",
            "CREATE TABLE cat (id INTEGER NOT NULL PRIMARY KEY, poet_id INTEGER NOT NULL, text TEXT NOT NULL, parent_id INTEGER NOT NULL, url TEXT NOT NULL)",
            "CREATE TABLE poem (id INTEGER PRIMARY KEY, cat_id INTEGER, title NVARCHAR(255), url NVARCHAR(255))",
            "CREATE TABLE verse (poem_id INTEGER, vorder INTEGER, position INTEGER, text TEXT)",
            "CREATE TABLE poet_meta (id INTEGER NOT NULL PRIMARY KEY, slug TEXT NOT NULL)",
            "CREATE VIRTUAL TABLE verse_fts4 USING fts4(text, content='verse')",
        ).forEach { driver.execute(null, it, 0) }
        GetRandomGhazalForPoetUseCase(SarvDatabase(driver).verseQueries)
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
            val viewModel = PoetDetailViewModel(repository, getRandomGhazalForPoet, poetId = 2)

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
            val viewModel = PoetDetailViewModel(repository, getRandomGhazalForPoet, poetId = 2)

            viewModel.onAction(PoetDetailAction.OnCategoryToggle(categoryId = 24))

            val state = viewModel.state.value
            assertThat(state.categories).hasSize(2)
            assertThat(state.categories.last().title).isEqualTo("غزل ۱")
            assertThat(state.categories.last().depth).isEqualTo(1)
        }

    @Test
    fun `chat available when ghazal category is nested under a subcategory`() =
        runTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithCategories =
                        listOf(
                            PoetWithCategories(
                                poet =
                                    Poet(
                                        id = 7,
                                        name = "سعدی شیرازی",
                                        description = null,
                                        rootCatId = 118,
                                        imageUrl = null,
                                    ),
                                categories =
                                    listOf(
                                        PoetCategoryNode(
                                            id = 122,
                                            text = "دیوان اشعار",
                                            url = "/divan",
                                            children =
                                                listOf(
                                                    PoetCategoryNode(
                                                        id = 124,
                                                        text = "غزلیات",
                                                        url = "/ghazals",
                                                    ),
                                                ),
                                        ),
                                    ),
                            ),
                        )
                }
            val viewModel = PoetDetailViewModel(repository, getRandomGhazalForPoet, poetId = 7)

            assertThat(viewModel.state.value.canChat).isTrue()
        }

    @Test
    fun `chat unavailable when poet has no ghazal category`() =
        runTest {
            val repository =
                FakePoetRepository().apply {
                    poetsWithCategories =
                        listOf(
                            PoetWithCategories(
                                poet =
                                    Poet(
                                        id = 7,
                                        name = "سعدی شیرازی",
                                        description = null,
                                        rootCatId = 118,
                                        imageUrl = null,
                                    ),
                                categories =
                                    listOf(
                                        PoetCategoryNode(
                                            id = 123,
                                            text = "بوستان",
                                            url = "/boostan",
                                        ),
                                    ),
                            ),
                        )
                }
            val viewModel = PoetDetailViewModel(repository, getRandomGhazalForPoet, poetId = 7)

            assertThat(viewModel.state.value.canChat).isFalse()
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
            val viewModel = PoetDetailViewModel(repository, getRandomGhazalForPoet, poetId = 2)

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
