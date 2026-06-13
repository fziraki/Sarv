package abkabk.azbarkon.features.games.session

import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.games.GameConstants
import abkabk.azbarkon.domain.model.games.GameGenerationCache
import abkabk.azbarkon.domain.model.games.GameQuestion
import abkabk.azbarkon.domain.model.games.GameType
import abkabk.azbarkon.domain.repository.GamesRepository
import abkabk.azbarkon.testing.FakeUserPreferencesRepository
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameSessionViewModelTest {
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
    fun `hint is blocked when coin balance is below cost`() =
        runTest {
            val preferences = FakeUserPreferencesRepository()
            preferences.adjustCoinBalance(-700)
            val viewModel =
                GameSessionViewModel(
                    gameType = GameType.NEXT_VERSE,
                    gamesRepository = FakeGamesRepository(),
                    userPreferencesRepository = preferences,
                )

            assertThat(viewModel.state.value.canUseHint).isFalse()
        }

    @Test
    fun `hint deducts coins and disables one wrong option`() =
        runTest {
            val preferences = FakeUserPreferencesRepository()
            val viewModel =
                GameSessionViewModel(
                    gameType = GameType.NEXT_VERSE,
                    gamesRepository = FakeGamesRepository(),
                    userPreferencesRepository = preferences,
                )

            val balanceBefore = preferences.getCoinBalance()
            viewModel.onAction(GameSessionAction.OnHintClick)

            assertThat(viewModel.state.value.hintUsedThisQuiz).isTrue()
            assertThat(viewModel.state.value.disabledOptionIndices).isNotEmpty()
            assertThat(preferences.getCoinBalance()).isEqualTo(balanceBefore - GameConstants.HINT_COST)
        }

    @Test
    fun `skip without selection counts as no answer without deducting coins`() =
        runTest {
            val preferences = FakeUserPreferencesRepository()
            val viewModel =
                GameSessionViewModel(
                    gameType = GameType.NEXT_VERSE,
                    gamesRepository = FakeGamesRepository(),
                    userPreferencesRepository = preferences,
                )

            val balanceBefore = preferences.getCoinBalance()
            assertThat(viewModel.state.value.hasSelection).isFalse()
            assertThat(viewModel.state.value.canPressPrimaryAction).isTrue()

            viewModel.onAction(GameSessionAction.OnCheckAnswerClick)

            assertThat(viewModel.state.value.answerPhase).isEqualTo(QuizAnswerPhase.Wrong)
            assertThat(viewModel.state.value.noAnswerCount).isEqualTo(1)
            assertThat(viewModel.state.value.wrongCount).isEqualTo(0)
            assertThat(preferences.getCoinBalance()).isEqualTo(balanceBefore)
            assertThat(viewModel.state.value.sessionScoreDelta).isEqualTo(0)
        }

    @Test
    fun `hasSelection becomes true after option is selected`() =
        runTest {
            val viewModel =
                GameSessionViewModel(
                    gameType = GameType.NEXT_VERSE,
                    gamesRepository = FakeGamesRepository(),
                    userPreferencesRepository = FakeUserPreferencesRepository(),
                )

            assertThat(viewModel.state.value.hasSelection).isFalse()
            viewModel.onAction(GameSessionAction.OnOptionSelected(1))
            assertThat(viewModel.state.value.hasSelection).isTrue()
        }

    @Test
    fun `wrong answer increments wrong count immediately`() =
        runTest {
            val viewModel =
                GameSessionViewModel(
                    gameType = GameType.NEXT_VERSE,
                    gamesRepository = FakeGamesRepository(),
                    userPreferencesRepository = FakeUserPreferencesRepository(),
                )

            viewModel.onAction(GameSessionAction.OnOptionSelected(1))
            viewModel.onAction(GameSessionAction.OnCheckAnswerClick)

            assertThat(viewModel.state.value.answerPhase).isEqualTo(QuizAnswerPhase.Wrong)
            assertThat(viewModel.state.value.wrongCount).isEqualTo(1)
            assertThat(viewModel.state.value.noAnswerCount).isEqualTo(0)
        }

    @Test
    fun `second hint on same quiz does not deduct additional coins`() =
        runTest {
            val preferences = FakeUserPreferencesRepository()
            val viewModel =
                GameSessionViewModel(
                    gameType = GameType.NEXT_VERSE,
                    gamesRepository = FakeGamesRepository(),
                    userPreferencesRepository = preferences,
                )

            val balanceBefore = preferences.getCoinBalance()
            viewModel.onAction(GameSessionAction.OnHintClick)
            viewModel.onAction(GameSessionAction.OnHintClick)

            assertThat(preferences.getCoinBalance()).isEqualTo(balanceBefore - GameConstants.HINT_COST)
            assertThat(viewModel.state.value.hintUsedThisQuiz).isTrue()
        }

    @Test
    fun `correct answer on last quiz navigates to result summary`() =
        runTest {
            val events = mutableListOf<GameSessionEvent>()
            val viewModel =
                GameSessionViewModel(
                    gameType = GameType.NEXT_VERSE,
                    gamesRepository =
                        object : GamesRepository {
                            override fun createGenerationCache() = GameGenerationCache()

                            override suspend fun generateQuestion(
                                gameType: GameType,
                                sessionSeed: Long,
                                quizIndex: Int,
                                cache: GameGenerationCache,
                            ): Result<GameQuestion, abkabk.azbarkon.core.domain.result.DataError.Local> =
                                Result.Success(
                                    GameQuestion.NextVerse(
                                        promptLine = "prompt",
                                        poetName = "حافظ",
                                        options = listOf("correct", "wrong1", "wrong2", "wrong3"),
                                        correctIndex = 0,
                                    ),
                                )

                            override suspend fun generateQuizBatch(
                                gameType: GameType,
                                seed: Long,
                                count: Int,
                            ): Result<List<GameQuestion>, abkabk.azbarkon.core.domain.result.DataError.Local> =
                                Result.Success(
                                    List(count) {
                                        GameQuestion.NextVerse(
                                            promptLine = "prompt",
                                            poetName = "حافظ",
                                            options = listOf("correct", "wrong1", "wrong2", "wrong3"),
                                            correctIndex = 0,
                                        )
                                    },
                                )
                        },
                    userPreferencesRepository = FakeUserPreferencesRepository(),
                )

            val collectJob =
                launch {
                    viewModel.events.collect { events += it }
                }

            repeat(GameConstants.QUIZ_COUNT) { index ->
                viewModel.onAction(GameSessionAction.OnOptionSelected(0))
                viewModel.onAction(GameSessionAction.OnCheckAnswerClick)
                if (index < GameConstants.QUIZ_COUNT - 1) {
                    advanceTimeBy(1_600)
                }
            }
            advanceTimeBy(1_600)

            assertThat(events.filterIsInstance<GameSessionEvent.NavigateToResult>().single().summary.correctCount)
                .isEqualTo(GameConstants.QUIZ_COUNT)
            collectJob.cancel()
        }

    private class FakeGamesRepository : GamesRepository {
        override fun createGenerationCache() = GameGenerationCache()

        override suspend fun generateQuestion(
            gameType: GameType,
            sessionSeed: Long,
            quizIndex: Int,
            cache: GameGenerationCache,
        ): Result<GameQuestion, abkabk.azbarkon.core.domain.result.DataError.Local> =
            Result.Success(createQuestion())

        override suspend fun generateQuizBatch(
            gameType: GameType,
            seed: Long,
            count: Int,
        ): Result<List<GameQuestion>, abkabk.azbarkon.core.domain.result.DataError.Local> =
            Result.Success(List(count) { createQuestion() })

        private fun createQuestion() =
            GameQuestion.NextVerse(
                promptLine = "prompt",
                poetName = "حافظ",
                options = listOf("correct", "wrong1", "wrong2", "wrong3"),
                correctIndex = 0,
            )
    }
}
