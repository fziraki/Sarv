package abkabk.azbarkon.features.games.completepoem

import abkabk.azbarkon.domain.model.games.GameQuestion
import abkabk.azbarkon.features.games.components.GameInstructionText
import abkabk.azbarkon.features.games.components.GameOptionState
import abkabk.azbarkon.features.games.components.GamePoemCard
import abkabk.azbarkon.features.games.components.gameOptionColors
import abkabk.azbarkon.features.games.components.gameOptionStyle
import abkabk.azbarkon.features.games.components.optionStateForIndex
import abkabk.azbarkon.features.games.session.QuizAnswerPhase
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.game_complete_poem_instruction
import org.jetbrains.compose.resources.stringResource

@Composable
fun CompletePoemContent(
    question: GameQuestion.CompletePoem,
    filledWords: List<String>,
    disabledOptionIndices: Set<Int>,
    answerPhase: QuizAnswerPhase,
    enabled: Boolean,
    onWordSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val firstCorrectIndex = question.options.indexOf(question.correctWords.first)
    val secondCorrectIndex = question.options.indexOf(question.correctWords.second)

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
                    text = renderBlankedLine(question.blankedLine2, filledWords),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
            }
        }

        GameInstructionText(text = stringResource(Res.string.game_complete_poem_instruction))

        question.options.chunked(2).forEach { rowWords ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowWords.forEach { word ->
                    val index = question.options.indexOf(word)
                    val selectedIndex =
                        when {
                            filledWords.isNotEmpty() && word == filledWords.first() -> index
                            filledWords.size > 1 && word == filledWords[1] -> index
                            else -> null
                        }
                    val revealCorrectIndex =
                        when (answerPhase) {
                            QuizAnswerPhase.Correct,
                            QuizAnswerPhase.Wrong,
                            ->
                                if (word == question.correctWords.first) {
                                    firstCorrectIndex
                                } else if (word == question.correctWords.second) {
                                    secondCorrectIndex
                                } else {
                                    -1
                                }

                            QuizAnswerPhase.Answering -> -1
                        }
                    val effectiveCorrectIndex =
                        if (revealCorrectIndex >= 0) revealCorrectIndex else firstCorrectIndex
                    val state =
                        if (answerPhase != QuizAnswerPhase.Answering && index == revealCorrectIndex) {
                            GameOptionState.Correct
                        } else {
                            optionStateForIndex(
                                index = index,
                                selectedIndex = selectedIndex,
                                correctIndex = effectiveCorrectIndex,
                                disabledIndices = disabledOptionIndices,
                                answerPhase = answerPhase,
                            )
                        }
                    val (background, contentColor) = gameOptionColors(state)
                    val alreadyUsed = word in filledWords
                    val clickable =
                        enabled &&
                            !alreadyUsed &&
                            state != GameOptionState.Disabled &&
                            answerPhase == QuizAnswerPhase.Answering &&
                            filledWords.size < 2

                    Text(
                        modifier =
                            Modifier
                                .weight(1f)
                                .gameOptionStyle(state)
                                .clickable(enabled = clickable) { onWordSelected(word) }
                                .padding(horizontal = 12.dp, vertical = 14.dp),
                        text = word,
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

private fun renderBlankedLine(
    blankedLine: String,
    filledWords: List<String>,
): String {
    val parts = blankedLine.split("____")
    if (parts.size < 3) return blankedLine
    val first = filledWords.getOrElse(0) { "____" }
    val second = filledWords.getOrElse(1) { "____" }
    return parts[0] + first + parts[1] + second + parts[2]
}
