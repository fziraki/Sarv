package abkabk.azbarkon.features.games.organizepoem

import abkabk.azbarkon.domain.model.games.GameQuestion
import abkabk.azbarkon.domain.model.games.OrganizeLine
import abkabk.azbarkon.features.games.components.GameInstructionText
import abkabk.azbarkon.features.games.components.GameOptionState
import abkabk.azbarkon.features.games.components.GamePoemCard
import abkabk.azbarkon.features.games.components.GamePoemCorrectRevealText
import abkabk.azbarkon.features.games.components.gameOptionColors
import abkabk.azbarkon.features.games.components.gamePoemUserAnswerTextColor
import abkabk.azbarkon.features.games.session.QuizAnswerPhase
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.cd_drag_handle
import azbarkoncmp.shared.generated.resources.drag_handle
import azbarkoncmp.shared.generated.resources.game_organize_poem_instruction
import azbarkoncmp.shared.generated.resources.keep
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val POEM_LINE_COUNT = 4

@Composable
fun OrganizePoemContent(
    question: GameQuestion.OrganizePoem,
    orderedLineIds: List<String>,
    initialOrderedLineIds: List<String>,
    pinnedLineId: String?,
    answerPhase: QuizAnswerPhase,
    enabled: Boolean,
    onReorder: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lineById = question.lines.associateBy { it.id }
    val reorderEnabled = enabled && answerPhase == QuizAnswerPhase.Answering

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GamePoemCard(poetName = question.poetName) {
            OrganizePoemCardContent(
                question = question,
                lineById = lineById,
                orderedLineIds = orderedLineIds,
                initialOrderedLineIds = initialOrderedLineIds,
                answerPhase = answerPhase,
            )
        }

        GameInstructionText(text = stringResource(Res.string.game_organize_poem_instruction))

        ReorderablePoemLines(
            items = orderedLineIds,
            pinnedItemId = pinnedLineId,
            enabled = reorderEnabled,
            onReorder = onReorder,
            modifier = Modifier.fillMaxWidth(),
        ) { index, lineId ->
            val line = lineById[lineId] ?: return@ReorderablePoemLines
            val isPinned = lineId == pinnedLineId
            val isCorrectPosition =
                answerPhase != QuizAnswerPhase.Answering &&
                    question.correctOrder.getOrNull(index) == lineId
            val isWrongPosition =
                answerPhase == QuizAnswerPhase.Wrong &&
                    question.correctOrder.getOrNull(index) != lineId &&
                    orderedLineIds.indexOf(lineId) == index

            val state =
                when {
                    answerPhase != QuizAnswerPhase.Answering && isCorrectPosition ->
                        GameOptionState.Correct

                    isWrongPosition -> GameOptionState.Wrong
                    isPinned && answerPhase == QuizAnswerPhase.Answering -> GameOptionState.Disabled
                    else -> GameOptionState.Default
                }
            val (background, contentColor) = gameOptionColors(state)

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(background)
                        .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter =
                        painterResource(
                            if (isPinned && reorderEnabled) Res.drawable.keep else Res.drawable.drag_handle,
                        ),
                    contentDescription =
                        if (!isPinned && reorderEnabled) {
                            stringResource(Res.string.cd_drag_handle)
                        } else {
                            null
                        },
                    tint =
                        when {
                            isPinned && reorderEnabled -> MaterialTheme.colorScheme.outlineVariant
                            reorderEnabled -> MaterialTheme.colorScheme.onSurfaceVariant
                            else -> MaterialTheme.colorScheme.outlineVariant
                        },
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = line.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor,
                )
            }
        }
    }
}

@Composable
private fun OrganizePoemCardContent(
    question: GameQuestion.OrganizePoem,
    lineById: Map<String, OrganizeLine>,
    orderedLineIds: List<String>,
    initialOrderedLineIds: List<String>,
    answerPhase: QuizAnswerPhase,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (answerPhase == QuizAnswerPhase.Answering) {
            if (orderedLineIds == initialOrderedLineIds) {
                repeat(POEM_LINE_COUNT) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "…",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                orderedLineIds.forEach { lineId ->
                    val line = lineById[lineId] ?: return@forEach
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = line.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = gamePoemUserAnswerTextColor(),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        } else {
            question.correctOrder.forEach { lineId ->
                val line = lineById[lineId] ?: return@forEach
                GamePoemCorrectRevealText(text = line.text)
            }
        }
    }
}
