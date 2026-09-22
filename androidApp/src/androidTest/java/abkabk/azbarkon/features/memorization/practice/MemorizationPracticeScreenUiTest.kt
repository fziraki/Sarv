package abkabk.azbarkon.features.memorization.practice

import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.domain.model.memorization.SrsGrade
import abkabk.azbarkon.ui.theme.SarvTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import org.junit.Rule
import org.junit.Test

class MemorizationPracticeScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun practiceScreen_showsBottomPanel_whenShowFront() {
        composeTestRule.setContent {
            SarvTheme {
                MemorizationPracticeScreen(
                    state = MemorizationPracticeState(
                        screenState = UiScreenState.Success,
                        phase = PracticePhase.SHOW_FRONT,
                        currentCard = PracticeCardUi(
                            id = 1,
                            front = "بیا که قصر امل سخت سست بنیاد است",
                            back = "بیار باده که بنیاد عمر بر باد است",
                            expectedContinuation = "عیش آن است که کم گویی و دل بخواهی",
                        ),
                        totalCards = 5,
                        cardIndex = 1,
                    ),
                    onAction = {},
                )
            }
        }
        composeTestRule.onNodeWithTag("PracticeBottomPanel").assertIsDisplayed()
    }

    @Test
    fun practiceScreen_showsGradeButtons_whenRevealed() {
        composeTestRule.setContent {
            SarvTheme {
                MemorizationPracticeScreen(
                    state = MemorizationPracticeState(
                        screenState = UiScreenState.Success,
                        phase = PracticePhase.REVEALED,
                        currentCard = PracticeCardUi(
                            id = 1,
                            front = "بیا که قصر امل سخت سست بنیاد است",
                            back = "بیار باده که بنیاد عمر بر باد است",
                            expectedContinuation = "عیش آن است که کم گویی و دل بخواهی",
                        ),
                        totalCards = 5,
                        cardIndex = 1,
                        selectedGrade = SrsGrade.GOOD,
                    ),
                    onAction = {},
                )
            }
        }
        composeTestRule.onNodeWithTag("GradeGood").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SessionStatsBar").assertIsDisplayed()
    }

    @Test
    fun practiceScreen_showsCompleteScreen() {
        composeTestRule.setContent {
            SarvTheme {
                MemorizationPracticeScreen(
                    state = MemorizationPracticeState(
                        screenState = UiScreenState.Success,
                        phase = PracticePhase.COMPLETE,
                        sessionReviewed = 10,
                        sessionLearned = 3,
                        sessionMistakes = 1,
                    ),
                    onAction = {},
                )
            }
        }
        composeTestRule.onNodeWithTag("PracticeComplete").assertIsDisplayed()
    }
}
