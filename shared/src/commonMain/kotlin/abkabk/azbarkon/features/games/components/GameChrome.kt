package abkabk.azbarkon.features.games.components

import abkabk.azbarkon.domain.model.games.GameConstants
import abkabk.azbarkon.domain.model.games.GameType
import abkabk.azbarkon.features.games.session.QuizAnswerPhase
import abkabk.azbarkon.ui.components.AzbarkonButtonDefaults
import abkabk.azbarkon.ui.components.AzbarkonPrimaryButton
import abkabk.azbarkon.ui.components.Header
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.coin
import azbarkoncmp.shared.generated.resources.complete_poem_title
import azbarkoncmp.shared.generated.resources.game_check_answer
import azbarkoncmp.shared.generated.resources.game_check_arrangement
import azbarkoncmp.shared.generated.resources.game_confirm_answer
import azbarkoncmp.shared.generated.resources.game_hint
import azbarkoncmp.shared.generated.resources.game_hint_cost
import azbarkoncmp.shared.generated.resources.game_stage_format
import azbarkoncmp.shared.generated.resources.lightbulb
import azbarkoncmp.shared.generated.resources.next_line_title
import azbarkoncmp.shared.generated.resources.poetry_arrangement_title
import azbarkoncmp.shared.generated.resources.whois_poet_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameChrome(
    gameType: GameType,
    coinBalance: Int,
    currentQuizIndex: Int,
    timeRemainingSeconds: Int,
    canUseHint: Boolean,
    canCheckAnswer: Boolean,
    onBackClick: () -> Unit,
    onHintClick: () -> Unit,
    onCheckAnswerClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Header(
                title = gameTitle(gameType),
                onBackClick = onBackClick,
                modifier = Modifier.weight(1f),
            )
            GameCoinBadge(balance = coinBalance)
        }

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text =
                stringResource(
                    Res.string.game_stage_format,
                    currentQuizIndex + 1,
                    GameConstants.QUIZ_COUNT,
                ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        val progress =
            timeRemainingSeconds.toFloat() / GameConstants.TIME_LIMIT_SECONDS.toFloat()
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
        Text(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            text = timeRemainingSeconds.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
        )

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            content()
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GameHintButton(
                enabled = canUseHint,
                onClick = onHintClick,
                modifier = Modifier.weight(0.4f),
            )

            AzbarkonPrimaryButton(
                text = checkAnswerLabel(gameType),
                onClick = onCheckAnswerClick,
                enabled = canCheckAnswer,
                modifier = Modifier.weight(0.6f),
            )
        }
    }
}

@Composable
private fun GameHintButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = AzbarkonButtonDefaults.Shape,
        border =
            androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline,
            ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(Res.drawable.lightbulb),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = stringResource(Res.string.game_hint_cost),
                style = MaterialTheme.typography.labelSmall,
            )
            Text(
                text = stringResource(Res.string.game_hint),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

@Composable
private fun GameCoinBadge(balance: Int) {
    Row(
        modifier =
            Modifier
                .padding(end = 16.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(20.dp),
                ).padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(Res.drawable.coin),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = balance.toString(),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun gameTitle(gameType: GameType): String =
    when (gameType) {
        GameType.NEXT_VERSE -> stringResource(Res.string.next_line_title)
        GameType.FIND_POET -> stringResource(Res.string.whois_poet_title)
        GameType.COMPLETE_POEM -> stringResource(Res.string.complete_poem_title)
        GameType.ORGANIZE_POEM -> stringResource(Res.string.poetry_arrangement_title)
    }

@Composable
private fun checkAnswerLabel(gameType: GameType): String =
    when (gameType) {
        GameType.NEXT_VERSE -> stringResource(Res.string.game_confirm_answer)
        GameType.ORGANIZE_POEM -> stringResource(Res.string.game_check_arrangement)
        else -> stringResource(Res.string.game_check_answer)
    }

enum class GameOptionState {
    Default,
    Selected,
    Correct,
    Wrong,
    TimeoutReveal,
    Disabled,
}

@Composable
fun GamePoemCard(
    modifier: Modifier = Modifier,
    poetName: String? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(16.dp),
                ).padding(16.dp),
    ) {
        poetName?.let {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End,
            )
        }
        content()
    }
}

@Composable
fun GameInstructionText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.fillMaxWidth(),
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Start,
    )
}

fun optionStateForIndex(
    index: Int,
    selectedIndex: Int?,
    correctIndex: Int,
    disabledIndices: Set<Int>,
    answerPhase: QuizAnswerPhase,
): GameOptionState {
    if (index in disabledIndices) return GameOptionState.Disabled
    return when (answerPhase) {
        QuizAnswerPhase.Answering ->
            if (index == selectedIndex) GameOptionState.Selected else GameOptionState.Default

        QuizAnswerPhase.Correct ->
            when {
                index == correctIndex -> GameOptionState.Correct
                index == selectedIndex -> GameOptionState.Wrong
                else -> GameOptionState.Default
            }

        QuizAnswerPhase.Wrong ->
            when {
                index == correctIndex -> GameOptionState.Correct
                index == selectedIndex -> GameOptionState.Wrong
                else -> GameOptionState.Default
            }

        QuizAnswerPhase.Timeout ->
            when {
                index == correctIndex -> GameOptionState.TimeoutReveal
                else -> GameOptionState.Default
            }
    }
}

@Composable
fun gameOptionColors(state: GameOptionState): Pair<androidx.compose.ui.graphics.Color, androidx.compose.ui.graphics.Color> =
    when (state) {
        GameOptionState.Default ->
            MaterialTheme.colorScheme.surfaceContainer to MaterialTheme.colorScheme.onSurface

        GameOptionState.Disabled ->
            MaterialTheme.colorScheme.surfaceDim to MaterialTheme.colorScheme.onSurfaceVariant

        GameOptionState.Selected,
        GameOptionState.Correct,
        ->
            MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary

        GameOptionState.Wrong ->
            MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError

        GameOptionState.TimeoutReveal ->
            MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
    }
