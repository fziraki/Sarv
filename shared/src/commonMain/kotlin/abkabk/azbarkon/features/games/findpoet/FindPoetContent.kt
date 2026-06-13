package abkabk.azbarkon.features.games.findpoet

import abkabk.azbarkon.domain.model.games.GameQuestion
import abkabk.azbarkon.features.games.components.GameInstructionText
import abkabk.azbarkon.features.games.components.GameOptionState
import abkabk.azbarkon.features.games.components.GamePoemCard
import abkabk.azbarkon.features.games.components.gameOptionColors
import abkabk.azbarkon.features.games.components.gameOptionStyle
import abkabk.azbarkon.features.games.components.optionStateForIndex
import abkabk.azbarkon.features.games.session.QuizAnswerPhase
import abkabk.azbarkon.ui.components.NetworkImage
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.game_find_poet_instruction
import org.jetbrains.compose.resources.stringResource

@Composable
fun FindPoetContent(
    question: GameQuestion.FindPoet,
    selectedPoetId: Int?,
    disabledOptionIndices: Set<Int>,
    answerPhase: QuizAnswerPhase,
    enabled: Boolean,
    onPoetSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val correctIndex = question.options.indexOfFirst { it.id == question.correctPoetId }
    val selectedIndex = question.options.indexOfFirst { it.id == selectedPoetId }.takeIf { it >= 0 }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        GamePoemCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = question.line1,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = question.line2,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
            }
        }

        GameInstructionText(text = stringResource(Res.string.game_find_poet_instruction))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            question.options.chunked(2).forEach { rowOptions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    rowOptions.forEach { poetOption ->
                        val index = question.options.indexOf(poetOption)
                        val state =
                            optionStateForIndex(
                                index = index,
                                selectedIndex = selectedIndex,
                                correctIndex = correctIndex,
                                disabledIndices = disabledOptionIndices,
                                answerPhase = answerPhase,
                            )
                        val (background, contentColor) = gameOptionColors(state)
                        val clickable =
                            enabled &&
                                state != GameOptionState.Disabled &&
                                answerPhase == QuizAnswerPhase.Answering

                        Row(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .gameOptionStyle(state)
                                    .clickable(enabled = clickable) {
                                        onPoetSelected(poetOption.id)
                                    }.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (poetOption.imageUrl != null) {
                                NetworkImage(
                                    imageUrl = poetOption.imageUrl,
                                    modifier =
                                        Modifier
                                            .size(36.dp)
                                            .clip(CircleShape),
                                )
                            }
                            Text(
                                modifier = Modifier.weight(1f),
                                text = poetOption.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = contentColor,
                            )
                        }
                    }
                }
            }
        }
    }
}
