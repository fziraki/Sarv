package abkabk.azbarkon.features.games.session

import abkabk.azbarkon.domain.model.games.GameType
import abkabk.azbarkon.core.ui_base.BaseScreen
import abkabk.azbarkon.core.ui_base.UiScreenState
import abkabk.azbarkon.ui.components.AzbarkonPrimaryButton
import abkabk.azbarkon.ui.components.AzbarkonSecondaryButton
import abkabk.azbarkon.ui.components.Header
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.complete_poem_title
import azbarkoncmp.shared.generated.resources.game_back_to_list
import azbarkoncmp.shared.generated.resources.game_replay
import azbarkoncmp.shared.generated.resources.game_result_correct
import azbarkoncmp.shared.generated.resources.game_result_score
import azbarkoncmp.shared.generated.resources.game_result_time
import azbarkoncmp.shared.generated.resources.game_result_time_format
import azbarkoncmp.shared.generated.resources.game_result_title
import azbarkoncmp.shared.generated.resources.game_result_wrong
import azbarkoncmp.shared.generated.resources.next_line_title
import azbarkoncmp.shared.generated.resources.poetry_arrangement_title
import azbarkoncmp.shared.generated.resources.whois_poet_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun GameResultRoot(
    gameType: GameType,
    correctCount: Int,
    wrongCount: Int,
    totalSeconds: Int,
    scoreDelta: Int,
    onReplayClick: () -> Unit,
    onBackToListClick: () -> Unit,
) {
    BaseScreen(screenState = UiScreenState.Success) {
        GameResultScreen(
            gameType = gameType,
            correctCount = correctCount,
            wrongCount = wrongCount,
            totalSeconds = totalSeconds,
            scoreDelta = scoreDelta,
            onReplayClick = onReplayClick,
            onBackToListClick = onBackToListClick,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
fun GameResultScreen(
    gameType: GameType,
    correctCount: Int,
    wrongCount: Int,
    totalSeconds: Int,
    scoreDelta: Int,
    onReplayClick: () -> Unit,
    onBackToListClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Header(
            title = stringResource(Res.string.game_result_title),
            subtitle = gameTitle(gameType),
            onBackClick = onBackToListClick,
        )

        ResultStatRow(
            label = stringResource(Res.string.game_result_correct),
            value = correctCount.toString(),
        )
        ResultStatRow(
            label = stringResource(Res.string.game_result_wrong),
            value = wrongCount.toString(),
        )
        ResultStatRow(
            label = stringResource(Res.string.game_result_time),
            value = stringResource(Res.string.game_result_time_format, totalSeconds),
        )
        ResultStatRow(
            label = stringResource(Res.string.game_result_score),
            value = scoreDelta.toString(),
        )

        AzbarkonPrimaryButton(
            text = stringResource(Res.string.game_replay),
            onClick = onReplayClick,
            modifier = Modifier.fillMaxWidth(),
        )
        AzbarkonSecondaryButton(
            text = stringResource(Res.string.game_back_to_list),
            onClick = onBackToListClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ResultStatRow(
    label: String,
    value: String,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Start,
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
