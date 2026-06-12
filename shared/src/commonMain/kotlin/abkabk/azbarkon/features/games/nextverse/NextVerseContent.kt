package abkabk.azbarkon.features.games.nextverse

import abkabk.azbarkon.domain.model.games.GameQuestion
import abkabk.azbarkon.features.games.components.GameInstructionText
import abkabk.azbarkon.features.games.components.GameOptionState
import abkabk.azbarkon.features.games.components.GamePoemCard
import abkabk.azbarkon.features.games.components.gameOptionColors
import abkabk.azbarkon.features.games.components.optionStateForIndex
import abkabk.azbarkon.features.games.session.QuizAnswerPhase
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.game_next_verse_instruction
import org.jetbrains.compose.resources.stringResource

@Composable
fun NextVerseContent(
    question: GameQuestion.NextVerse,
    selectedOptionIndex: Int?,
    disabledOptionIndices: Set<Int>,
    answerPhase: QuizAnswerPhase,
    enabled: Boolean,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GameInstructionText(text = stringResource(Res.string.game_next_verse_instruction))

        GamePoemCard(poetName = question.poetName) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = question.promptLine,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }

        question.options.forEachIndexed { index, option ->
            val state =
                optionStateForIndex(
                    index = index,
                    selectedIndex = selectedOptionIndex,
                    correctIndex = question.correctIndex,
                    disabledIndices = disabledOptionIndices,
                    answerPhase = answerPhase,
                )
            val (background, contentColor) = gameOptionColors(state)
            val clickable = enabled && state != GameOptionState.Disabled && answerPhase == QuizAnswerPhase.Answering

            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(background)
                        .clickable(enabled = clickable) { onOptionSelected(index) }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                text = option,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                textAlign = TextAlign.Center,
            )
        }
    }
}
