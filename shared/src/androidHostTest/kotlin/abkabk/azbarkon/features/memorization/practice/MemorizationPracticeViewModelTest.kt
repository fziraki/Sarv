package abkabk.azbarkon.features.memorization.practice

import abkabk.azbarkon.domain.model.memorization.SrsCard
import abkabk.azbarkon.domain.model.memorization.SrsGrade
import abkabk.azbarkon.testing.FakeMemorizationRepository
import assertk.assertThat
import assertk.assertions.isEqualTo
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
class MemorizationPracticeViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: FakeMemorizationRepository

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeMemorizationRepository()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads due cards into session`() =
        runTest {
            repository.dueCards =
                abkabk.azbarkon.core.domain.result.Result.Success(
                    listOf(
                        sampleCard(id = 1),
                        sampleCard(id = 2),
                    ),
                )

            val viewModel = MemorizationPracticeViewModel(repository, poemId = null)
            val state = viewModel.state.value

            assertThat(state.currentCard?.id).isEqualTo(1)
            assertThat(state.totalCards).isEqualTo(2)
            assertThat(state.phase).isEqualTo(PracticePhase.SHOW_FRONT)
        }

    @Test
    fun `submitting grade updates card via repository`() =
        runTest {
            val card = sampleCard(id = 5)
            repository.dueCards = abkabk.azbarkon.core.domain.result.Result.Success(listOf(card))
            repository.reviewResult =
                abkabk.azbarkon.core.domain.result.Result.Success(
                    card.copy(interval = 3, consecutiveCorrect = 1),
                )

            val viewModel = MemorizationPracticeViewModel(repository, poemId = 10)
            viewModel.onAction(MemorizationPracticeAction.OnRevealClick)
            viewModel.onAction(MemorizationPracticeAction.OnGradeClick(SrsGrade.GOOD))

            assertThat(repository.lastReviewedCardId).isEqualTo(5)
            assertThat(repository.lastReviewGrade).isEqualTo(SrsGrade.GOOD)
            assertThat(viewModel.state.value.phase).isEqualTo(PracticePhase.FEEDBACK)
        }

    @Test
    fun `empty due queue completes session`() =
        runTest {
            repository.dueCards = abkabk.azbarkon.core.domain.result.Result.Success(emptyList())

            val viewModel = MemorizationPracticeViewModel(repository, poemId = null)

            assertThat(viewModel.state.value.phase).isEqualTo(PracticePhase.COMPLETE)
            assertThat(viewModel.state.value.currentCard).isEqualTo(null)
        }

    private fun sampleCard(id: Long) =
        SrsCard(
            id = id,
            poemId = 10,
            cardIndex = 0,
            front = "front",
            back = "back",
            interval = 0,
            ease = 2.5,
            dueDateMillis = 0,
            consecutiveCorrect = 0,
        )
}
