package abkabk.azbarkon.features.games

import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.domain.model.games.GameQuestion
import abkabk.azbarkon.domain.model.games.GameType
import abkabk.azbarkon.domain.model.games.OrganizeLine
import abkabk.azbarkon.domain.model.games.PoetOption
import abkabk.azbarkon.features.games.session.GameResultScreen
import abkabk.azbarkon.features.games.session.GameSessionScreen
import abkabk.azbarkon.features.games.session.GameSessionState
import abkabk.azbarkon.ui.theme.SarvTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class GamesScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun gamesScreen_showsAllFourGameTypes() {
        composeTestRule.setContent {
            SarvTheme {
                GamesScreen(onNavigateToGame = {})
            }
        }
        composeTestRule.onNodeWithTag("GamesGrid").assertIsDisplayed()
        composeTestRule.onNodeWithTag("GameItem_NEXT_VERSE").assertIsDisplayed()
        composeTestRule.onNodeWithTag("GameItem_COMPLETE_POEM").assertIsDisplayed()
        composeTestRule.onNodeWithTag("GameItem_FIND_POET").assertIsDisplayed()
        composeTestRule.onNodeWithTag("GameItem_ORGANIZE_POEM").assertIsDisplayed()
    }

    @Test
    fun gamesScreen_clickGameItem_firesCallback() {
        var navigatedTo: GameType? = null
        composeTestRule.setContent {
            SarvTheme {
                GamesScreen(onNavigateToGame = { navigatedTo = it })
            }
        }
        composeTestRule.onNodeWithTag("GameItem_NEXT_VERSE").performClick()
        assert(navigatedTo == GameType.NEXT_VERSE) { "Expected NEXT_VERSE but was $navigatedTo" }
    }

    @Test
    fun session_showsTopBar_withBackButton() {
        composeTestRule.setContent {
            SarvTheme {
                GameSessionScreen(
                    state = GameSessionState(
                        screenState = UiScreenState.Success,
                        gameType = GameType.NEXT_VERSE,
                        coinBalance = 100,
                        currentQuizIndex = 0,
                        questions = listOf(nextVerseQuestion),
                    ),
                    onAction = {},
                )
            }
        }
        composeTestRule.onNodeWithTag("GameBackButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("CoinBadge").assertIsDisplayed()
    }

    @Test
    fun session_primaryButton_showsCheck() {
        composeTestRule.setContent {
            SarvTheme {
                GameSessionScreen(
                    state = GameSessionState(
                        screenState = UiScreenState.Success,
                        gameType = GameType.NEXT_VERSE,
                        coinBalance = 100,
                        currentQuizIndex = 0,
                        questions = listOf(nextVerseQuestion),
                    ),
                    onAction = {},
                )
            }
        }
        composeTestRule.onNodeWithTag("PrimaryActionButton").assertIsDisplayed()
    }

    @Test
    fun session_hintButton_visible() {
        composeTestRule.setContent {
            SarvTheme {
                GameSessionScreen(
                    state = GameSessionState(
                        screenState = UiScreenState.Success,
                        gameType = GameType.NEXT_VERSE,
                        coinBalance = 100,
                        currentQuizIndex = 0,
                        questions = listOf(nextVerseQuestion),
                    ),
                    onAction = {},
                )
            }
        }
        composeTestRule.onNodeWithTag("HintButton").assertIsDisplayed()
    }

    @Test
    fun session_nextVerse_showsOptions() {
        composeTestRule.setContent {
            SarvTheme {
                GameSessionScreen(
                    state = GameSessionState(
                        screenState = UiScreenState.Success,
                        gameType = GameType.NEXT_VERSE,
                        coinBalance = 100,
                        currentQuizIndex = 0,
                        questions = listOf(nextVerseQuestion),
                    ),
                    onAction = {},
                )
            }
        }
        composeTestRule.onNodeWithTag("NextVerseOption_0").assertIsDisplayed()
        composeTestRule.onNodeWithTag("NextVerseOption_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("NextVerseOption_2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("NextVerseOption_3").assertIsDisplayed()
    }

    @Test
    fun session_nextVerse_selectOption_firesAction() {
        var selected: Int? = null
        composeTestRule.setContent {
            SarvTheme {
                GameSessionScreen(
                    state = GameSessionState(
                        screenState = UiScreenState.Success,
                        gameType = GameType.NEXT_VERSE,
                        coinBalance = 100,
                        currentQuizIndex = 0,
                        questions = listOf(nextVerseQuestion),
                    ),
                    onAction = { action ->
                        if (action is abkabk.azbarkon.features.games.session.GameSessionAction.OnOptionSelected) {
                            selected = action.index
                        }
                    },
                )
            }
        }
        composeTestRule.onNodeWithTag("NextVerseOption_2").performClick()
        assert(selected == 2) { "Expected selected index 2 but was $selected" }
    }

    @Test
    fun session_findPoet_showsPoetOptions() {
        composeTestRule.setContent {
            SarvTheme {
                GameSessionScreen(
                    state = GameSessionState(
                        screenState = UiScreenState.Success,
                        gameType = GameType.FIND_POET,
                        coinBalance = 100,
                        currentQuizIndex = 0,
                        questions = listOf(findPoetQuestion),
                    ),
                    onAction = {},
                )
            }
        }
        composeTestRule.onNodeWithTag("FindPoetOption_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("FindPoetOption_2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("FindPoetOption_3").assertIsDisplayed()
        composeTestRule.onNodeWithTag("FindPoetOption_4").assertIsDisplayed()
    }

    @Test
    fun session_completePoem_showsWordOptions() {
        composeTestRule.setContent {
            SarvTheme {
                GameSessionScreen(
                    state = GameSessionState(
                        screenState = UiScreenState.Success,
                        gameType = GameType.COMPLETE_POEM,
                        coinBalance = 100,
                        currentQuizIndex = 0,
                        questions = listOf(completePoemQuestion),
                    ),
                    onAction = {},
                )
            }
        }
        composeTestRule.onNodeWithTag("CompletePoemOption_0").assertIsDisplayed()
        composeTestRule.onNodeWithTag("CompletePoemOption_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("CompletePoemOption_2").assertIsDisplayed()
    }

    @Test
    fun session_organizePoem_showsLines() {
        composeTestRule.setContent {
            SarvTheme {
                GameSessionScreen(
                    state = GameSessionState(
                        screenState = UiScreenState.Success,
                        gameType = GameType.ORGANIZE_POEM,
                        coinBalance = 100,
                        currentQuizIndex = 0,
                        questions = listOf(organizePoemQuestion),
                        orderedLineIds = listOf("l1", "l2", "l3", "l4"),
                        initialOrderedLineIds = listOf("l1", "l2", "l3", "l4"),
                    ),
                    onAction = {},
                )
            }
        }
        composeTestRule.onNodeWithTag("OrganizeLine_l1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("OrganizeLine_l2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("OrganizeLine_l3").assertIsDisplayed()
        composeTestRule.onNodeWithTag("OrganizeLine_l4").assertIsDisplayed()
    }

    @Test
    fun result_showsScoreCard() {
        composeTestRule.setContent {
            SarvTheme {
                GameResultScreen(
                    correctCount = 5,
                    wrongCount = 3,
                    noAnswerCount = 2,
                    scoreDelta = 40,
                    onReplayClick = {},
                    onBackToListClick = {},
                )
            }
        }
        composeTestRule.onNodeWithTag("GameResultScreen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ResultScoreCard").assertIsDisplayed()
    }

    @Test
    fun result_showsStats() {
        composeTestRule.setContent {
            SarvTheme {
                GameResultScreen(
                    correctCount = 5,
                    wrongCount = 3,
                    noAnswerCount = 2,
                    scoreDelta = 40,
                    onReplayClick = {},
                    onBackToListClick = {},
                )
            }
        }
        composeTestRule.onNodeWithTag("ResultCorrectStat").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ResultWrongStat").assertIsDisplayed()
        composeTestRule.onNodeWithTag("ResultNoAnswerStat").assertIsDisplayed()
    }

    @Test
    fun result_replayButton_clickable() {
        var clicked = false
        composeTestRule.setContent {
            SarvTheme {
                GameResultScreen(
                    correctCount = 5,
                    wrongCount = 3,
                    noAnswerCount = 2,
                    scoreDelta = 40,
                    onReplayClick = { clicked = true },
                    onBackToListClick = {},
                )
            }
        }
        composeTestRule.onNodeWithTag("ReplayButton").performClick()
        assert(clicked) { "Replay button was not clicked" }
    }

    @Test
    fun result_backToListButton_clickable() {
        var clicked = false
        composeTestRule.setContent {
            SarvTheme {
                GameResultScreen(
                    correctCount = 5,
                    wrongCount = 3,
                    noAnswerCount = 2,
                    scoreDelta = 40,
                    onReplayClick = {},
                    onBackToListClick = { clicked = true },
                )
            }
        }
        composeTestRule.onNodeWithTag("BackToListButton").performClick()
        assert(clicked) { "Back to list button was not clicked" }
    }
}

private val nextVerseQuestion = GameQuestion.NextVerse(
    promptLine = "بیا که قصر امل سخت سست بنیاد است",
    poetName = "صائب تبریزی",
    options = listOf("بیار باده که بنیاد عمر بر باد است", "ز دریا می‌خواهم که بی‌پایان است", "دل من در هوایت بی‌قرار است", "شب تاریک و بیم موج و گردابی چنین هایل"),
    correctIndex = 0,
)

private val findPoetQuestion = GameQuestion.FindPoet(
    rightHemistich = "بیا که قصر امل سخت سست بنیاد است",
    leftHemistich = "بیار باده که بنیاد عمر بر باد است",
    options = listOf(
        PoetOption(id = 1, name = "صائب تبریزی", imageUrl = null),
        PoetOption(id = 2, name = "حافظ شیرازی", imageUrl = null),
        PoetOption(id = 3, name = "سعدی شیرازی", imageUrl = null),
        PoetOption(id = 4, name = "مولوی بلخی", imageUrl = null),
    ),
    correctPoetId = 1,
)

private val completePoemQuestion = GameQuestion.CompletePoem(
    rightHemistich = "بیا که قصر امل سخت سست بنیاد است",
    blankedLeftHemistich = "بیار باده که بنیاد عمر بر باد است",
    poetName = "صائب تبریزی",
    options = listOf("بیار", "باده", "بنیاد", "عمر"),
    correctWords = "بیار" to "باده",
)

private val organizePoemQuestion = GameQuestion.OrganizePoem(
    poetName = "صائب تبریزی",
    lines = listOf(
        OrganizeLine(id = "l1", text = "بیا که قصر امل سخت سست بنیاد است"),
        OrganizeLine(id = "l2", text = "بیار باده که بنیاد عمر بر باد است"),
        OrganizeLine(id = "l3", text = "عیش آن است که کم گویی و دل بخواهی"),
        OrganizeLine(id = "l4", text = "صفا عیش آن است که کم گویی و دل بخواهی"),
    ),
    correctOrder = listOf("l1", "l2", "l3", "l4"),
)
