package abkabk.azbarkon.features.games.session

import abkabk.azbarkon.core.ui_base.BaseScreen
import abkabk.azbarkon.core.ui_base.ObserveAsEvents
import abkabk.azbarkon.domain.model.games.GameQuestion
import abkabk.azbarkon.domain.model.games.GameSessionSummary
import abkabk.azbarkon.domain.model.games.GameType
import abkabk.azbarkon.features.games.completepoem.CompletePoemContent
import abkabk.azbarkon.features.games.components.GameChrome
import abkabk.azbarkon.features.games.findpoet.FindPoetContent
import abkabk.azbarkon.features.games.navigation.GameTypeRoute
import abkabk.azbarkon.features.games.navigation.toDomain
import abkabk.azbarkon.features.games.nextverse.NextVerseContent
import abkabk.azbarkon.features.games.organizepoem.OrganizePoemContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun GameSessionRoot(
    gameTypeRoute: GameTypeRoute,
    onBackClick: () -> Unit,
    onNavigateToResult: (GameType, GameSessionSummary) -> Unit,
) {
    val viewModel: GameSessionViewModel =
        koinViewModel {
            parametersOf(gameTypeRoute.toDomain())
        }
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            GameSessionEvent.NavigateBack -> onBackClick()
            is GameSessionEvent.NavigateToResult ->
                onNavigateToResult(event.gameType, event.summary)
        }
    }

    BaseScreen(
        screenState = state.screenState,
        onRetry = { viewModel.onAction(GameSessionAction.OnRetryClick) },
    ) {
        GameSessionScreen(
            state = state,
            onAction = viewModel::onAction,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun GameSessionScreen(
    state: GameSessionState,
    onAction: (GameSessionAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    GameChrome(
        gameType = state.gameType,
        coinBalance = state.coinBalance,
        currentQuizIndex = state.currentQuizIndex,
        timeRemainingSeconds = state.timeRemainingSeconds,
        canUseHint = state.canUseHint,
        canCheckAnswer = state.canCheckAnswer,
        onBackClick = { onAction(GameSessionAction.OnBackClick) },
        onHintClick = { onAction(GameSessionAction.OnHintClick) },
        onCheckAnswerClick = { onAction(GameSessionAction.OnCheckAnswerClick) },
        modifier = modifier,
    ) {
        when (val question = state.currentQuestion) {
            is GameQuestion.NextVerse ->
                NextVerseContent(
                    question = question,
                    selectedOptionIndex = state.selectedOptionIndex,
                    disabledOptionIndices = state.disabledOptionIndices,
                    answerPhase = state.answerPhase,
                    enabled = state.isAnswering,
                    onOptionSelected = { onAction(GameSessionAction.OnOptionSelected(it)) },
                )

            is GameQuestion.FindPoet ->
                FindPoetContent(
                    question = question,
                    selectedPoetId = state.selectedPoetId,
                    disabledOptionIndices = state.disabledOptionIndices,
                    answerPhase = state.answerPhase,
                    enabled = state.isAnswering,
                    onPoetSelected = { onAction(GameSessionAction.OnPoetSelected(it)) },
                )

            is GameQuestion.CompletePoem ->
                CompletePoemContent(
                    question = question,
                    filledWords = state.filledWords,
                    disabledOptionIndices = state.disabledOptionIndices,
                    answerPhase = state.answerPhase,
                    enabled = state.isAnswering,
                    onWordSelected = { onAction(GameSessionAction.OnWordSelected(it)) },
                )

            is GameQuestion.OrganizePoem ->
                OrganizePoemContent(
                    question = question,
                    orderedLineIds = state.orderedLineIds,
                    pinnedLineId = state.pinnedLineId,
                    answerPhase = state.answerPhase,
                    enabled = state.isAnswering,
                    onReorder = { from, to ->
                        onAction(GameSessionAction.OnReorderLines(from, to))
                    },
                )

            null -> Unit
        }
    }
}
