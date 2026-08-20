package abkabk.azbarkon.features.chat

import abkabk.azbarkon.core.uidata.UiScreenState
import androidx.compose.runtime.Stable

@Stable
data class ChatState(
    val screenState: UiScreenState = UiScreenState.Idle,
    val poetName: String = "",
    val poetImageUrl: String? = null,
    val messages: List<ChatMessageUi> = emptyList(),
    val inputText: String = "",
    val isPoetTyping: Boolean = false,
)

sealed interface ChatAction {
    data object OnLoad : ChatAction

    data object OnBackClick : ChatAction

    data class OnInputChange(
        val text: String,
    ) : ChatAction

    data object OnSendClick : ChatAction

    data class OnPoetMessageLongPress(
        val messageId: String,
    ) : ChatAction
}

sealed interface ChatEvent {
    data object NavigateBack : ChatEvent
}
