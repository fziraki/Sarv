package abkabk.azbarkon.features.poets

import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.core.uidata.UiText
import abkabk.azbarkon.domain.model.PoemDetail
import abkabk.azbarkon.domain.model.PoemVerse
import abkabk.azbarkon.features.poems.details.PoemDetailAction
import abkabk.azbarkon.features.poems.details.PoemDetailEvent
import abkabk.azbarkon.features.poems.details.PoemDetailViewModel
import abkabk.azbarkon.features.poems.details.PoemVersePositionType
import abkabk.azbarkon.domain.usecase.BuildShareTextUseCase
import abkabk.azbarkon.domain.usecase.StartMemorizationFromPoemUseCase
import abkabk.azbarkon.testing.FakeAudioPlayer
import abkabk.azbarkon.testing.FakeMemorizationRepository
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
import sarv.shared.generated.resources.Res
import sarv.shared.generated.resources.search_not_found_in_poem
import abkabk.azbarkon.testing.cancelScope
import abkabk.azbarkon.testing.runViewModelTest
import org.junit.jupiter.api.Test

class PoemDetailViewModelTest {

    @Test
    fun `loads poem detail by id with verse position types`() =
        runViewModelTest {
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
                    memorizationRepository = FakeMemorizationRepository(),
                    shareService = FakeShareService(),
                    buildShareText = BuildShareTextUseCase(),
                    startMemorizationFromPoem = StartMemorizationFromPoemUseCase(FakeMemorizationRepository()),
                    poemId = 77,
                    player = FakeAudioPlayer(),
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
            viewModel.cancelScope()
        }

    @Test
    fun `toggle like updates state`() =
        runViewModelTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails = mapOf(77 to samplePoemDetail(singleVerse = true))
                }
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    memorizationRepository = FakeMemorizationRepository(),
                    shareService = FakeShareService(),
                    buildShareText = BuildShareTextUseCase(),
                    startMemorizationFromPoem = StartMemorizationFromPoemUseCase(FakeMemorizationRepository()),
                    poemId = 77,
                    player = FakeAudioPlayer(),
                )

            viewModel.onAction(PoemDetailAction.OnLikeClick)

            assertThat(viewModel.state.value.isLiked).isTrue()
            viewModel.cancelScope()
        }

    @Test
    fun `toggle bookmark updates state`() =
        runViewModelTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails = mapOf(77 to samplePoemDetail(singleVerse = true))
                }
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    memorizationRepository = FakeMemorizationRepository(),
                    shareService = FakeShareService(),
                    buildShareText = BuildShareTextUseCase(),
                    startMemorizationFromPoem = StartMemorizationFromPoemUseCase(FakeMemorizationRepository()),
                    poemId = 77,
                    player = FakeAudioPlayer(),
                )

            viewModel.onAction(PoemDetailAction.OnBookmarkClick)

            assertThat(viewModel.state.value.isBookmarked).isTrue()
            viewModel.cancelScope()
        }

    @Test
    fun `find submit highlights and scrolls to first matching verse`() =
        runViewModelTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails = mapOf(77 to samplePoemDetail())
                }
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    memorizationRepository = FakeMemorizationRepository(),
                    shareService = FakeShareService(),
                    buildShareText = BuildShareTextUseCase(),
                    startMemorizationFromPoem = StartMemorizationFromPoemUseCase(FakeMemorizationRepository()),
                    poemId = 77,
                    player = FakeAudioPlayer(),
                )

            viewModel.onAction(PoemDetailAction.OnFindQueryChange("الا"))
            viewModel.onAction(PoemDetailAction.OnFindSubmit)

            val state = viewModel.state.value
            assertThat(state.highlightQuery).isEqualTo("الا")
            assertThat(state.scrollToVerseId).isEqualTo("1-0")
            viewModel.cancelScope()
        }

    @Test
    fun `find submit with no match shows snackbar`() =
        runViewModelTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails = mapOf(77 to samplePoemDetail(singleVerse = true))
                }
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    memorizationRepository = FakeMemorizationRepository(),
                    shareService = FakeShareService(),
                    buildShareText = BuildShareTextUseCase(),
                    startMemorizationFromPoem = StartMemorizationFromPoemUseCase(FakeMemorizationRepository()),
                    poemId = 77,
                    player = FakeAudioPlayer(),
                )

            viewModel.onAction(PoemDetailAction.OnFindQueryChange("زلف"))
            viewModel.onAction(PoemDetailAction.OnFindSubmit)

            val error = viewModel.state.value.screenState as UiScreenState.Error
            assertThat(error.message).isEqualTo(UiText.Resource(Res.string.search_not_found_in_poem))

            assertThat(viewModel.state.value.highlightQuery).isEqualTo("")
            assertThat(viewModel.state.value.scrollToVerseId).isNull()
            viewModel.cancelScope()
        }

    @Test
    fun `search click toggles find bar`() =
        runViewModelTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails = mapOf(77 to samplePoemDetail(singleVerse = true))
                }
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    memorizationRepository = FakeMemorizationRepository(),
                    shareService = FakeShareService(),
                    buildShareText = BuildShareTextUseCase(),
                    startMemorizationFromPoem = StartMemorizationFromPoemUseCase(FakeMemorizationRepository()),
                    poemId = 77,
                    player = FakeAudioPlayer(),
                )

            viewModel.onAction(PoemDetailAction.OnSearchClick)
            assertThat(viewModel.state.value.isFindBarVisible).isTrue()

            viewModel.onAction(PoemDetailAction.OnSearchClick)
            assertThat(viewModel.state.value.isFindBarVisible).isFalse()
            viewModel.cancelScope()
        }

    @Test
    fun `find bar close hides bar and clears highlight`() =
        runViewModelTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails = mapOf(77 to samplePoemDetail(singleVerse = true))
                }
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    memorizationRepository = FakeMemorizationRepository(),
                    shareService = FakeShareService(),
                    buildShareText = BuildShareTextUseCase(),
                    startMemorizationFromPoem = StartMemorizationFromPoemUseCase(FakeMemorizationRepository()),
                    poemId = 77,
                    player = FakeAudioPlayer(),
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
            viewModel.cancelScope()
        }

    @Test
    fun `share copies poem text to share service`() =
        runViewModelTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails = mapOf(77 to samplePoemDetail(singleVerse = true))
                }
            val shareService = FakeShareService()
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    memorizationRepository = FakeMemorizationRepository(),
                    shareService = shareService,
                    buildShareText = BuildShareTextUseCase(),
                    startMemorizationFromPoem = StartMemorizationFromPoemUseCase(FakeMemorizationRepository()),
                    poemId = 77,
                    player = FakeAudioPlayer(),
                )

            viewModel.onAction(PoemDetailAction.OnShareClick)

            assertThat(shareService.lastSharedText).isEqualTo("حافظ\nغزل شماره ۷\n\nبیت اول")
            assertThat(shareService.lastSharedTitle).isEqualTo("غزل شماره ۷")
            viewModel.cancelScope()
        }

    @Test
    fun `share uses copied text when text was copied`() =
        runViewModelTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails = mapOf(77 to samplePoemDetail())
                }
            val shareService = FakeShareService()
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    memorizationRepository = FakeMemorizationRepository(),
                    shareService = shareService,
                    buildShareText = BuildShareTextUseCase(),
                    startMemorizationFromPoem = StartMemorizationFromPoemUseCase(FakeMemorizationRepository()),
                    poemId = 77,
                    player = FakeAudioPlayer(),
                )

            viewModel.onAction(PoemDetailAction.OnTextCopied("متن انتخابی از شعر"))
            viewModel.onAction(PoemDetailAction.OnShareClick)

            assertThat(shareService.lastSharedText).isEqualTo(
                "حافظ\nغزل شماره ۷\n\nمتن انتخابی از شعر",
            )
            viewModel.cancelScope()
        }

    @Test
    fun `tasvirnegar with copied text emits event carrying it`() =
        runViewModelTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails = mapOf(77 to samplePoemDetail())
                }
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    memorizationRepository = FakeMemorizationRepository(),
                    shareService = FakeShareService(),
                    buildShareText = BuildShareTextUseCase(),
                    startMemorizationFromPoem = StartMemorizationFromPoemUseCase(FakeMemorizationRepository()),
                    poemId = 77,
                    player = FakeAudioPlayer(),
                )

            viewModel.events.test {
                viewModel.onAction(PoemDetailAction.OnTextCopied("متن انتخابی از شعر"))
                viewModel.onAction(PoemDetailAction.OnImageCreatorClick)

                assertThat(awaitItem()).isEqualTo(
                    PoemDetailEvent.NavigateToTasvirNegar(
                        initialText = "متن انتخابی از شعر",
                    ),
                )
                cancelAndIgnoreRemainingEvents()
            }
            viewModel.cancelScope()
        }

    @Test
    fun `tasvirnegar without copied text emits event with null initial text`() =
        runViewModelTest {
            val repository =
                FakePoemRepository().apply {
                    poemDetails = mapOf(77 to samplePoemDetail(singleVerse = true))
                }
            val viewModel =
                PoemDetailViewModel(
                    poemRepository = repository,
                    savedPoemRepository = FakeSavedPoemRepository(),
                    memorizationRepository = FakeMemorizationRepository(),
                    shareService = FakeShareService(),
                    buildShareText = BuildShareTextUseCase(),
                    startMemorizationFromPoem = StartMemorizationFromPoemUseCase(FakeMemorizationRepository()),
                    poemId = 77,
                    player = FakeAudioPlayer(),
                )

            viewModel.events.test {
                viewModel.onAction(PoemDetailAction.OnImageCreatorClick)

                assertThat(awaitItem()).isEqualTo(
                    PoemDetailEvent.NavigateToTasvirNegar(initialText = null),
                )
                cancelAndIgnoreRemainingEvents()
            }
            viewModel.cancelScope()
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
