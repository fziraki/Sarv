package abkabk.azbarkon.features.chat

import abkabk.azbarkon.core.domain.result.onFailure
import abkabk.azbarkon.core.domain.result.onSuccess
import abkabk.azbarkon.core.uidata.BaseViewModel
import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.core.uidata.UiText
import abkabk.azbarkon.core.uidata.toUiText
import abkabk.azbarkon.core.util.currentTimeMillis
import abkabk.azbarkon.domain.platform.ClipboardService
import abkabk.azbarkon.domain.repository.ChatRepository
import abkabk.azbarkon.domain.repository.PoetRepository
import androidx.lifecycle.viewModelScope
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.chat_persian_only
import azbarkoncmp.shared.generated.resources.poem_copied
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class ChatViewModel(
    private val poetRepository: PoetRepository,
    private val chatRepository: ChatRepository,
    private val clipboardService: ClipboardService,
    private val poetId: Int,
    private val replyDelayMillis: Long = 1_000L,
    private val random: Random = Random.Default,
) : BaseViewModel<ChatAction, ChatState, ChatEvent>(
        initialState = ChatState(),
    ) {
    init {
        onAction(ChatAction.OnLoad)
    }

    override fun onAction(action: ChatAction) {
        when (action) {
            ChatAction.OnLoad -> loadPoet()

            ChatAction.OnBackClick -> {
                viewModelScope.launch {
                    sendEvent(ChatEvent.NavigateBack)
                }
            }

            is ChatAction.OnInputChange -> {
                setState { copy(inputText = action.text) }
            }

            ChatAction.OnSendClick -> sendMessage()

            is ChatAction.OnPoetMessageLongPress -> copyPoetMessage(action.messageId)
        }
    }

    private fun loadPoet() {
        viewModelScope.launch {
            setState { copy(screenState = UiScreenState.Loading) }

            poetRepository.getPoetWithCategories(poetId)
                .onSuccess { poetWithCategories ->
                    setState {
                        copy(
                            screenState = UiScreenState.Success,
                            poetName = poetWithCategories.poet.name.orEmpty(),
                            poetImageUrl = poetWithCategories.poet.imageUrl,
                        )
                    }
                }.onFailure { error ->
                    val message = error.toUiText()
                    setState {
                        copy(screenState = UiScreenState.Error(message = message))
                    }
                }
        }
    }

    private fun sendMessage() {
        val trimmedInput = state.value.inputText.trim()
        if (trimmedInput.isEmpty()) return

        val lastLetter = extractLastPersianLetter(trimmedInput)
        if (lastLetter == null) {
            viewModelScope.launch {
                setState {
                    copy(screenState = UiScreenState.Error(
                        message = UiText.Resource(Res.string.chat_persian_only))
                    )
                }
            }
            return
        }

        val userMessage =
            ChatMessageUi(
                id = nextMessageId(),
                isFromUser = true,
                text = trimmedInput,
                timeLabel = formatChatTimeLabel(),
            )

        setState {
            copy(
                messages = messages + userMessage,
                inputText = "",
                isPoetTyping = true,
            )
        }

        viewModelScope.launch {
            delay(replyDelayMillis)
            replyToUser(lastLetter)
        }
    }

    private suspend fun replyToUser(lastLetter: Char) {

        chatRepository
            .findDistichByLastLetter(
                poetId = poetId,
                lastLetter = lastLetter,
            ).onSuccess { distich ->
                appendPoetMessage(
                    text = "${distich.rightText}\n\n${distich.leftText}",
                )
            }.onFailure { error ->
                setState {
                    copy(
                        isPoetTyping = false,
                        screenState = UiScreenState.Error(message = error.toUiText()))
                }
            }
    }

    private fun appendPoetMessage(text: String) {
        val poetMessage =
            ChatMessageUi(
                id = nextMessageId(),
                isFromUser = false,
                text = text,
                timeLabel = formatChatTimeLabel(),
            )

        setState {
            copy(
                messages = messages + poetMessage,
                isPoetTyping = false,
            )
        }
    }

    private fun copyPoetMessage(messageId: String) {
        val message = state.value.messages.find { it.id == messageId } ?: return
        if (message.text.isBlank()) return

        clipboardService.copyToClipboard(message.text)
        viewModelScope.launch {
            setState {
                copy(screenState = UiScreenState.Error(
                    message = UiText.Resource(Res.string.poem_copied),
                    isSuccess = true),
                )
            }
        }
    }

    private fun nextMessageId(): String = "${currentTimeMillis()}-${random.nextInt()}"
}
