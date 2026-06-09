package abkabk.azbarkon.features.chat

import abkabk.azbarkon.core.util.currentTimeMillis
import androidx.compose.runtime.Stable

@Stable
data class ChatMessageUi(
    val id: String,
    val isFromUser: Boolean,
    val text: String,
    val timeLabel: String,
)

fun formatChatTimeLabel(epochMillis: Long = currentTimeMillis()): String {
    val totalMinutes = ((epochMillis / 60_000) % (24 * 60)).toInt()
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
}
