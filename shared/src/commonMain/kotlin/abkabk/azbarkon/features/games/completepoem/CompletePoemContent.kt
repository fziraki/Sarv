package abkabk.azbarkon.features.games.completepoem

import abkabk.azbarkon.domain.model.games.GameQuestion
import abkabk.azbarkon.features.games.components.GameInstructionText
import abkabk.azbarkon.features.games.components.GameOptionState
import abkabk.azbarkon.features.games.components.GamePoemCard
import abkabk.azbarkon.features.games.components.gameOptionColors
import abkabk.azbarkon.features.games.components.gameOptionStyle
import abkabk.azbarkon.features.games.components.gamePoemCorrectAnswerTextColor
import abkabk.azbarkon.features.games.components.gamePoemUserAnswerTextColor
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import sarv.shared.generated.resources.Res
import sarv.shared.generated.resources.game_complete_poem_instruction
import org.jetbrains.compose.resources.stringResource
import abkabk.azbarkon.core.designsystem.SarvDimensions

private const val MIN_BLANK_PARTS = 3

@Composable
fun CompletePoemContent(
    question: GameQuestion.CompletePoem,
    filledWords: List<String>,
    disabledOptionIndices: Set<Int>,
    answerPhase: QuizAnswerPhase,
    enabled: Boolean,
    onWordSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val firstCorrectIndex = question.options.indexOf(question.correctWords.first)
    val secondCorrectIndex = question.options.indexOf(question.correctWords.second)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SarvDimensions.dimen12),
    ) {

        GamePoemCard(poetName = question.poetName) {
            Column(verticalArrangement = Arrangement.spacedBy(SarvDimensions.dimen8)) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = question.line1,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
                CompletePoemBlankedLine(
                    blankedLine = question.blankedLine2,
                    filledWords = filledWords,
                    answerPhase = answerPhase,
                    correctWords = question.correctWords,
                )
            }
        }

        GameInstructionText(text = stringResource(Res.string.game_complete_poem_instruction))

        question.options.chunked(2).forEach { rowWords ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SarvDimensions.dimen8),
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
                    val clickable =
                        enabled &&
                            answerPhase == QuizAnswerPhase.Answering &&
                            state != GameOptionState.Disabled &&
                            (word in filledWords || filledWords.size < 2)

                    Text(
                        modifier =
                            Modifier
                                .weight(1f)
                                .gameOptionStyle(state)
                                .clickable(enabled = clickable) { onWordSelect(word) }
                                .padding(horizontal = SarvDimensions.dimen12, vertical = SarvDimensions.dimen14),
                        text =
                            completePoemOptionLabel(
                                word = word,
                                filledWords = filledWords,
                                answerPhase = answerPhase,
                                correctWords = question.correctWords,
                            ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

private fun completePoemOptionLabel(
    word: String,
    filledWords: List<String>,
    answerPhase: QuizAnswerPhase,
    correctWords: Pair<String, String>,
): String {
    val order =
        when {
            answerPhase == QuizAnswerPhase.Answering ->
                filledWords.indexOf(word).takeIf { it >= 0 }?.plus(1)

            word == correctWords.first -> 1
            word == correctWords.second -> 2
            else -> null
        }
    return order?.let { "($it) $word" } ?: word
}

@Composable
private fun CompletePoemBlankedLine(
    blankedLine: String,
    filledWords: List<String>,
    answerPhase: QuizAnswerPhase,
    correctWords: Pair<String, String>,
) {
    val parts = blankedLine.split("____")
    if (parts.size < MIN_BLANK_PARTS) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = blankedLine,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        return
    }

    val userAnswerColor = gamePoemUserAnswerTextColor()
    val correctAnswerColor = gamePoemCorrectAnswerTextColor()
    val firstBlank =
        if (answerPhase == QuizAnswerPhase.Answering) {
            filledWords.getOrElse(0) { "____" }
        } else {
            correctWords.first
        }
    val secondBlank =
        if (answerPhase == QuizAnswerPhase.Answering) {
            filledWords.getOrElse(1) { "____" }
        } else {
            correctWords.second
        }
    val firstStyle =
        SpanStyle(
            color =
                if (answerPhase == QuizAnswerPhase.Answering && filledWords.isNotEmpty()) {
                    userAnswerColor
                } else if (answerPhase != QuizAnswerPhase.Answering) {
                    correctAnswerColor
                } else {
                    MaterialTheme.colorScheme.onBackground
                },
        )
    val secondStyle =
        SpanStyle(
            color =
                if (answerPhase == QuizAnswerPhase.Answering && filledWords.size > 1) {
                    userAnswerColor
                } else if (answerPhase != QuizAnswerPhase.Answering) {
                    correctAnswerColor
                } else {
                    MaterialTheme.colorScheme.onBackground
                },
        )

    Text(
        modifier = Modifier.fillMaxWidth(),
        text =
            buildAnnotatedString {
                append(parts[0])
                withStyle(firstStyle) {
                    append(firstBlank)
                }
                append(parts[1])
                withStyle(secondStyle) {
                    append(secondBlank)
                }
                append(parts[2])
            },
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
    )
}
