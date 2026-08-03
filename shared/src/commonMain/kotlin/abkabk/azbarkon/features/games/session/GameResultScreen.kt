package abkabk.azbarkon.features.games.session

import abkabk.azbarkon.core.uidata.BaseScreen
import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.domain.model.games.GameConstants
import abkabk.azbarkon.domain.model.games.GameType
import abkabk.azbarkon.ui.components.AzbarkonButtonDefaults
import abkabk.azbarkon.ui.components.Header
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.complete_poem_title
import azbarkoncmp.shared.generated.resources.game_back_to_list
import azbarkoncmp.shared.generated.resources.game_replay
import azbarkoncmp.shared.generated.resources.game_result_correct
import azbarkoncmp.shared.generated.resources.game_result_no_answer
import azbarkoncmp.shared.generated.resources.game_result_progress_answered_format
import azbarkoncmp.shared.generated.resources.game_result_progress_percent_format
import azbarkoncmp.shared.generated.resources.game_result_progress_question_count_label
import azbarkoncmp.shared.generated.resources.game_result_progress_total_questions_format
import azbarkoncmp.shared.generated.resources.game_result_score
import azbarkoncmp.shared.generated.resources.game_result_title
import azbarkoncmp.shared.generated.resources.game_result_wrong
import azbarkoncmp.shared.generated.resources.next_line_title
import azbarkoncmp.shared.generated.resources.poetry_arrangement_title
import azbarkoncmp.shared.generated.resources.replay
import azbarkoncmp.shared.generated.resources.whois_poet_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val PERCENT_MULTIPLIER = 100

@Composable
fun GameResultRoot(
    correctCount: Int,
    wrongCount: Int,
    noAnswerCount: Int,
    scoreDelta: Int,
    onReplayClick: () -> Unit,
    onBackToListClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BaseScreen(
        screenState = UiScreenState.Success,
        modifier = modifier,
    ) {
        GameResultScreen(
            correctCount = correctCount,
            wrongCount = wrongCount,
            noAnswerCount = noAnswerCount,
            scoreDelta = scoreDelta,
            onReplayClick = onReplayClick,
            onBackToListClick = onBackToListClick,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun GameResultScreen(
    correctCount: Int,
    wrongCount: Int,
    noAnswerCount: Int,
    scoreDelta: Int,
    onReplayClick: () -> Unit,
    onBackToListClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Header(
            title = stringResource(Res.string.game_result_title),
            onBackClick = onBackToListClick,
        )

        GameResultScoreCard(scoreDelta = scoreDelta)

        GameResultStatsRow(
            correctCount = correctCount,
            wrongCount = wrongCount,
            noAnswerCount = noAnswerCount,
        )

        GameResultProgressCard(
            correctCount = correctCount,
            wrongCount = wrongCount,
        )

        GameResultReplayButton(
            onClick = onReplayClick,
            modifier = Modifier.fillMaxWidth(),
        )

        GameResultBackToListButton(
            onClick = onBackToListClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun GameResultScoreCard(
    scoreDelta: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = scoreDelta.toString(),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "◆",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Text(
                    text = stringResource(Res.string.game_result_score),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    text = "◆",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
        }

    }
}

@Composable
private fun GameResultStatsRow(
    correctCount: Int,
    wrongCount: Int,
    noAnswerCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GameResultStatCard(
            value = noAnswerCount.toString(),
            label = stringResource(Res.string.game_result_no_answer),
            valueColor = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )

        GameResultStatCard(
            value = wrongCount.toString(),
            label = stringResource(Res.string.game_result_wrong),
            valueColor = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f),
        )

        GameResultStatCard(
            value = correctCount.toString(),
            label = stringResource(Res.string.game_result_correct),
            valueColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun GameResultStatCard(
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            color = valueColor,
            textAlign = TextAlign.Center,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun GameResultProgressCard(
    correctCount: Int,
    wrongCount: Int,
    modifier: Modifier = Modifier,
) {
    val answeredCount = correctCount + wrongCount
    val percent = (answeredCount * PERCENT_MULTIPLIER) / GameConstants.QUIZ_COUNT

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp),
                ).background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.game_result_progress_question_count_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text =
                    stringResource(
                        Res.string.game_result_progress_total_questions_format,
                        GameConstants.QUIZ_COUNT,
                    ),
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            GameResultSegmentedBar(
                filledCount = answeredCount,
                total = GameConstants.QUIZ_COUNT,
            )
            Text(
                text =
                    stringResource(
                        Res.string.game_result_progress_answered_format,
                        answeredCount,
                        GameConstants.QUIZ_COUNT,
                    ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        GameResultCircularProgress(
            percent = percent,
        )
    }
}

@Composable
private fun GameResultCircularProgress(
    percent: Int,
    modifier: Modifier = Modifier,
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier.size(88.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 6.dp.toPx()
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * (percent / 100f),
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text =
                    stringResource(
                        Res.string.game_result_progress_percent_format,
                        percent,
                    ),
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                color = MaterialTheme.colorScheme.onSurface,
            )

        }
    }
}

@Composable
private fun GameResultSegmentedBar(
    filledCount: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(total) { index ->
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (index < filledCount) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                        ),
            )
        }
    }
}

@Composable
private fun GameResultReplayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = AzbarkonButtonDefaults.Shape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Text(
                text = stringResource(Res.string.game_replay),
                style = MaterialTheme.typography.bodyMedium,
            )
            Icon(
                painter = painterResource(Res.drawable.replay),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )

        }
    }
}

@Composable
private fun GameResultBackToListButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = AzbarkonButtonDefaults.Shape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
            ),
    ) {
        Text(
            text = stringResource(Res.string.game_back_to_list),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Preview
@Composable
private fun GameResultScreenPreview() {
    AzbarkonTheme {
        GameResultScreen(
            correctCount = 2,
            wrongCount = 1,
            noAnswerCount = 7,
            scoreDelta = 20,
            onReplayClick = {},
            onBackToListClick = {},
        )
    }
}
