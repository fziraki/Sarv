package abkabk.azbarkon.features.chat

import abkabk.azbarkon.core.util.currentTimeMillis
import abkabk.azbarkon.core.util.localTimezoneOffsetMillis
import androidx.compose.runtime.Stable

private const val MILLIS_PER_MINUTE = 60_000L
private const val MINUTES_PER_HOUR = 60
private const val MINUTES_PER_DAY = MINUTES_PER_HOUR * 24

@Stable
data class ChatMessageUi(
    val id: String,
    val isFromUser: Boolean,
    val text: String,
    val timeLabel: String,
)

fun formatChatTimeLabel(epochMillis: Long = currentTimeMillis()): String {
    val localMillis = epochMillis + localTimezoneOffsetMillis()
    val totalMinutes = ((localMillis / MILLIS_PER_MINUTE) % MINUTES_PER_DAY).toInt()
    val hours = totalMinutes / MINUTES_PER_HOUR
    val minutes = totalMinutes % MINUTES_PER_HOUR
    return "${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
}
