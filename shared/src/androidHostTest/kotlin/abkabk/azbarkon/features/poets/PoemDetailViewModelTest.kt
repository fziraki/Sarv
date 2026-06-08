package abkabk.azbarkon.features.poets

import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.core.ui_base.UiText
import abkabk.azbarkon.domain.model.PoemDetail
import abkabk.azbarkon.domain.model.PoemVerse
import abkabk.azbarkon.features.poems.details.PoemDetailAction
import abkabk.azbarkon.features.poems.details.PoemDetailEvent
import abkabk.azbarkon.features.poems.details.PoemDetailViewModel
import abkabk.azbarkon.features.poems.details.PoemVersePositionType
import abkabk.azbarkon.testing.FakeSavedPoemRepository
import abkabk.azbarkon.testing.FakePoemRepository
import abkabk.azbarkon.testing.FakeShareService
import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import assertk.assertions.isTrue
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.search_not_found_in_poem
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
class PoemDetailViewModelTest {
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
    fun `loads poem detail by id with verse position types`() =
        runTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails =
                        mapOf(
                            77 to samplePoemDetail(),
                        )
                }
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    shareService = FakeShareService(),
                    poemId = 77,
                )

            val state = viewModel.state.value
            assertThat(state.screenState).isInstanceOf(UiScreenState.Success::class)
            assertThat(state.poetName).isEqualTo("حافظ")
            assertThat(state.subtitle).isEqualTo("غزل شماره ۷")
            assertThat(state.verses.size).isEqualTo(3)
            assertThat(state.verses[0].positionType).isEqualTo(PoemVersePositionType.Right)
            assertThat(state.verses[1].positionType).isEqualTo(PoemVersePositionType.Left)
            assertThat(state.verses[2].positionType).isEqualTo(PoemVersePositionType.Comment)
            assertThat(state.highlightQuery).isEqualTo("")
        }

    @Test
    fun `toggle like updates state`() =
        runTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails = mapOf(77 to samplePoemDetail(singleVerse = true))
                }
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    shareService = FakeShareService(),
                    poemId = 77,
                )

            viewModel.onAction(PoemDetailAction.OnLikeClick)

            assertThat(viewModel.state.value.isLiked).isTrue()
        }

    @Test
    fun `toggle bookmark updates state`() =
        runTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails = mapOf(77 to samplePoemDetail(singleVerse = true))
                }
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    shareService = FakeShareService(),
                    poemId = 77,
                )

            viewModel.onAction(PoemDetailAction.OnBookmarkClick)

            assertThat(viewModel.state.value.isBookmarked).isTrue()
        }

    @Test
    fun `find submit highlights and scrolls to first matching verse`() =
        runTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails = mapOf(77 to samplePoemDetail())
                }
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    shareService = FakeShareService(),
                    poemId = 77,
                )

            viewModel.onAction(PoemDetailAction.OnFindQueryChange("الا"))
            viewModel.onAction(PoemDetailAction.OnFindSubmit)

            val state = viewModel.state.value
            assertThat(state.highlightQuery).isEqualTo("الا")
            assertThat(state.scrollToVerseId).isEqualTo("1-0")
        }

    @Test
    fun `find submit with no match shows snackbar`() =
        runTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails = mapOf(77 to samplePoemDetail(singleVerse = true))
                }
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    shareService = FakeShareService(),
                    poemId = 77,
                )

            viewModel.events.test {
                viewModel.onAction(PoemDetailAction.OnFindQueryChange("زلف"))
                viewModel.onAction(PoemDetailAction.OnFindSubmit)

                assertThat(awaitItem()).isEqualTo(
                    PoemDetailEvent.ShowSnackbar(
                        UiText.Resource(Res.string.search_not_found_in_poem),
                    ),
                )
            }

            assertThat(viewModel.state.value.highlightQuery).isEqualTo("")
            assertThat(viewModel.state.value.scrollToVerseId).isNull()
        }

    @Test
    fun `search click toggles find bar`() =
        runTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails = mapOf(77 to samplePoemDetail(singleVerse = true))
                }
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    shareService = FakeShareService(),
                    poemId = 77,
                )

            viewModel.onAction(PoemDetailAction.OnSearchClick)
            assertThat(viewModel.state.value.isFindBarVisible).isTrue()

            viewModel.onAction(PoemDetailAction.OnSearchClick)
            assertThat(viewModel.state.value.isFindBarVisible).isFalse()
        }

    @Test
    fun `find bar close hides bar and clears highlight`() =
        runTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails = mapOf(77 to samplePoemDetail(singleVerse = true))
                }
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    shareService = FakeShareService(),
                    poemId = 77,
                )

            viewModel.onAction(PoemDetailAction.OnSearchClick)
            viewModel.onAction(PoemDetailAction.OnFindQueryChange("بیت"))
            viewModel.onAction(PoemDetailAction.OnFindSubmit)
            assertThat(viewModel.state.value.highlightQuery).isEqualTo("بیت")

            viewModel.onAction(PoemDetailAction.OnFindBarClose)

            val state = viewModel.state.value
            assertThat(state.isFindBarVisible).isFalse()
            assertThat(state.highlightQuery).isEqualTo("")
            assertThat(state.findInput).isEqualTo("")
            assertThat(state.scrollToVerseId).isNull()
        }

    @Test
    fun `share copies poem text to share service`() =
        runTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails = mapOf(77 to samplePoemDetail(singleVerse = true))
                }
            val shareService = FakeShareService()
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    shareService = shareService,
                    poemId = 77,
                )

            viewModel.onAction(PoemDetailAction.OnShareClick)

            assertThat(shareService.lastSharedText).isEqualTo("حافظ\nغزل شماره ۷\n\nبیت اول")
            assertThat(shareService.lastSharedTitle).isEqualTo("غزل شماره ۷")
        }

    private fun samplePoemDetail(singleVerse: Boolean = false): PoemDetail =
        if (singleVerse) {
            PoemDetail(
                id = 77,
                title = "غزل شماره ۷",
                poetName = "حافظ",
                categoryName = "غزلیات",
                verses =
                    listOf(
                        PoemVerse(
                            poemId = 77,
                            vorder = 1,
                            position = 0,
                            text = "بیت اول",
                        ),
                    ),
            )
        } else {
            PoemDetail(
                id = 77,
                title = "غزل شماره ۷",
                poetName = "حافظ",
                categoryName = "غزلیات",
                verses =
                    listOf(
                        PoemVerse(
                            poemId = 77,
                            vorder = 1,
                            position = 0,
                            text = "الا یا ایها الساقی ادر کاسا و ناولها",
                        ),
                        PoemVerse(
                            poemId = 77,
                            vorder = 1,
                            position = 1,
                            text = "که شد پیراهنت خونچکان من الایام",
                        ),
                        PoemVerse(
                            poemId = 77,
                            vorder = 0,
                            position = -1,
                            text = "شرح",
                        ),
                    ),
            )
        }
}
