package abkabk.azbarkon.features.games.organizepoem

import abkabk.azbarkon.domain.model.games.GameQuestion
import abkabk.azbarkon.features.games.components.GameInstructionText
import abkabk.azbarkon.features.games.components.GameOptionState
import abkabk.azbarkon.features.games.components.gameOptionColors
import abkabk.azbarkon.features.games.session.QuizAnswerPhase
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.cd_drag_handle
import azbarkoncmp.shared.generated.resources.drag_handle
import azbarkoncmp.shared.generated.resources.game_organize_poem_instruction
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun OrganizePoemContent(
    question: GameQuestion.OrganizePoem,
    orderedLineIds: List<String>,
    pinnedLineId: String?,
    answerPhase: QuizAnswerPhase,
    enabled: Boolean,
    onReorder: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lineById = question.lines.associateBy { it.id }
    val lazyListState = rememberLazyListState()
    val reorderableState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            if (enabled && answerPhase == QuizAnswerPhase.Answering) {
                onReorder(from.index, to.index)
            }
        }

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            GameInstructionText(text = stringResource(Res.string.game_organize_poem_instruction))
        }

        itemsIndexed(orderedLineIds, key = { _, lineId -> lineId }) { index, lineId ->
            val line = lineById[lineId] ?: return@itemsIndexed
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
                    answerPhase == QuizAnswerPhase.Timeout && isCorrectPosition ->
                        GameOptionState.TimeoutReveal

                    answerPhase != QuizAnswerPhase.Answering && isCorrectPosition ->
                        GameOptionState.Correct

                    isWrongPosition -> GameOptionState.Wrong
                    else -> GameOptionState.Default
                }
            val (background, contentColor) = gameOptionColors(state)

            ReorderableItem(reorderableState, key = lineId) { isDragging ->
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
                    if (!isPinned && enabled && answerPhase == QuizAnswerPhase.Answering) {
                        Icon(
                            painter = painterResource(Res.drawable.drag_handle),
                            contentDescription = stringResource(Res.string.cd_drag_handle),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier =
                                Modifier
                                    .draggableHandle(),
                        )
                    } else {
                        Icon(
                            painter = painterResource(Res.drawable.drag_handle),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(end = 4.dp),
                        )
                    }
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
}
