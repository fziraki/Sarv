package abkabk.azbarkon.features.chat

import abkabk.azbarkon.core.ui.keyboardAboveIme
import abkabk.azbarkon.core.ui.rememberKeyboardLiftPx
import abkabk.azbarkon.core.uidata.BaseScreen
import abkabk.azbarkon.core.uidata.LocalAzbarkonAppState
import abkabk.azbarkon.core.uidata.ObserveAsEvents
import abkabk.azbarkon.core.uidata.UiText
import abkabk.azbarkon.core.uidata.asString
import abkabk.azbarkon.features.poets.list.PoetAvatar
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.arrow_back_right
import azbarkoncmp.shared.generated.resources.cd_back
import azbarkoncmp.shared.generated.resources.cd_send_message
import azbarkoncmp.shared.generated.resources.chat_input_hint
import azbarkoncmp.shared.generated.resources.chat_poet_says
import azbarkoncmp.shared.generated.resources.chat_poet_typing
import azbarkoncmp.shared.generated.resources.chat_subtitle
import azbarkoncmp.shared.generated.resources.chat_title
import azbarkoncmp.shared.generated.resources.send
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val KEYBOARD_SCROLL_RETRY_DELAY_MILLIS = 100L
private const val SEND_ICON_ROTATION_DEGREES = 180f

@Immutable
private data class ChatColors(
    val userBubble: androidx.compose.ui.graphics.Color,
    val userBubbleText: androidx.compose.ui.graphics.Color,
    val poetBubble: androidx.compose.ui.graphics.Color,
    val poetBubbleText: androidx.compose.ui.graphics.Color,
    val poetBubbleBorder: androidx.compose.ui.graphics.Color,
    val accent: androidx.compose.ui.graphics.Color,
    val onAccent: androidx.compose.ui.graphics.Color,
    val inputBackground: androidx.compose.ui.graphics.Color,
    val inputBorder: androidx.compose.ui.graphics.Color,
    val inputText: androidx.compose.ui.graphics.Color,
    val inputPlaceholder: androidx.compose.ui.graphics.Color,
    val timestamp: androidx.compose.ui.graphics.Color,
)

@Composable
private fun chatColors(colorScheme: ColorScheme = MaterialTheme.colorScheme): ChatColors =
    ChatColors(
        userBubble = colorScheme.secondaryFixed,
        userBubbleText = colorScheme.onSecondaryFixed,
        poetBubble = colorScheme.tertiaryContainer,
        poetBubbleText = colorScheme.onTertiaryContainer,
        poetBubbleBorder = colorScheme.outlineVariant,
        accent = colorScheme.primary,
        onAccent = colorScheme.onPrimary,
        inputBackground = colorScheme.surfaceContainerLow,
        inputBorder = colorScheme.outlineVariant,
        inputText = colorScheme.onSurface,
        inputPlaceholder = colorScheme.onSurfaceVariant,
        timestamp = colorScheme.onSurfaceVariant,
    )

@Composable
fun ChatRoot(
    poetId: Int,
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = koinViewModel { parametersOf(poetId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appState = LocalAzbarkonAppState.current
    var snackbarMessage by remember { mutableStateOf<UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ChatEvent.NavigateBack -> onBackClick()
            is ChatEvent.ShowSnackbar -> snackbarMessage = event.message
        }
    }

    snackbarMessage?.let { message ->
        val resolvedMessage = message.asString()
        LaunchedEffect(resolvedMessage) {
            appState.showSnackbar(resolvedMessage)
            snackbarMessage = null
        }
    }

    BaseScreen(
        screenState = state.screenState,
        onRetry = { viewModel.onAction(ChatAction.OnRetryClick) },
    ) {
        ChatScreen(
            state = state,
            onAction = viewModel::onAction,
        )
    }
}

@Composable
fun ChatScreen(
    state: ChatState,
    onAction: (ChatAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val colors = chatColors()
    val keyboardLiftPx = rememberKeyboardLiftPx()

    LaunchedEffect(state.messages.size, state.isPoetTyping) {
        listState.scrollToLastMessage(state.messages.size, state.isPoetTyping)
    }

    LaunchedEffect(keyboardLiftPx, state.messages.size, state.isPoetTyping) {
        if (keyboardLiftPx > 0) {
            listState.scrollToLastMessage(state.messages.size, state.isPoetTyping)
            delay(KEYBOARD_SCROLL_RETRY_DELAY_MILLIS)
            listState.scrollToLastMessage(state.messages.size, state.isPoetTyping)
        }
    }

    Scaffold(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        topBar = {
            ChatTopBar(
                poetName = state.poetName,
                poetImageUrl = state.poetImageUrl,
                colors = colors,
                onBackClick = { onAction(ChatAction.OnBackClick) },
                isPoetTyping = state.isPoetTyping
            )
        },
        bottomBar = {

            ChatInputBar(
                value = state.inputText,
                colors = colors,
                onValueChange = { onAction(ChatAction.OnInputChange(it)) },
                onSendClick = { onAction(ChatAction.OnSendClick) },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding =
                PaddingValues(
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    bottom = paddingValues.calculateBottomPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(
                items = state.messages,
                key = { message -> message.id },
            ) { message ->
                if (message.isFromUser) {
                    UserMessageBubble(message = message, colors = colors)
                } else {
                    PoetMessageBubble(
                        message = message,
                        poetName = state.poetName,
                        colors = colors,
                        onLongPress = { onAction(ChatAction.OnPoetMessageLongPress(message.id)) },
                    )
                }
            }

        }

    }
}

@Composable
private fun ChatTopBar(
    poetName: String,
    poetImageUrl: String?,
    colors: ChatColors,
    onBackClick: () -> Unit,
    isPoetTyping: Boolean,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Icon(
            modifier = Modifier.clickable(onClick = onBackClick),
            painter = painterResource(Res.drawable.arrow_back_right),
            contentDescription = stringResource(Res.string.cd_back),
            tint = MaterialTheme.colorScheme.onBackground,
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = stringResource(Res.string.chat_title, poetName),
                style = MaterialTheme.typography.titleLarge,
                color = colors.accent,
                textAlign = TextAlign.End,
            )
            if (isPoetTyping) {
                Text(
                    text = stringResource(Res.string.chat_poet_typing, poetName),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.timestamp,
                    textAlign = TextAlign.End,
                )
            }else{
                Text(
                    text = stringResource(Res.string.chat_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.timestamp,
                    textAlign = TextAlign.End,
                )
            }
        }

        PoetAvatar(
            imageUrl = poetImageUrl,
            modifier =
                Modifier
                    .size(48.dp)
                    .border(2.dp, colors.accent.copy(alpha = 0.25f), CircleShape),
        )
    }
}

@Composable
private fun UserMessageBubble(
    message: ChatMessageUi,
    colors: ChatColors,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier =
                Modifier
                    .align(Alignment.CenterStart)
                    .widthIn(max = 280.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Box(
                modifier =
                    Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 18.dp,
                                topEnd = 18.dp,
                                bottomStart = 4.dp,
                                bottomEnd = 18.dp,
                            ),
                        ).background(colors.userBubble)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.userBubbleText,
                    textAlign = TextAlign.Start,
                )
            }

            Row(
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = message.timeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.timestamp,
                )
                Text(
                    text = "✓✓",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.accent.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                )
            }
        }
    }
}

@Composable
private fun PoetMessageBubble(
    message: ChatMessageUi,
    poetName: String,
    colors: ChatColors,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bubbleShape =
        RoundedCornerShape(
            topStart = 18.dp,
            topEnd = 18.dp,
            bottomStart = 18.dp,
            bottomEnd = 4.dp,
        )

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier =
                Modifier
                    .align(Alignment.CenterEnd)
                    .widthIn(max = 300.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Box(
                modifier =
                    Modifier
                        .clip(bubbleShape)
                        .background(colors.poetBubble)
                        .border(1.dp, colors.poetBubbleBorder, bubbleShape)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = onLongPress,
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = stringResource(Res.string.chat_poet_says, poetName),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.timestamp,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.poetBubbleText,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Text(
                text = message.timeLabel,
                style = MaterialTheme.typography.labelSmall,
                color = colors.timestamp,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, end = 4.dp),
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    colors: ChatColors,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
) {
    val pillShape = RoundedCornerShape(28.dp)

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .keyboardAboveIme()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(pillShape)
                    .background(colors.inputBackground)
                    .border(1.dp, colors.inputBorder, pillShape)
                    .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            IconButton(
                onClick = onSendClick,
                modifier =
                    Modifier
                        .padding(start = 4.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(colors.accent),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.send),
                    contentDescription = stringResource(Res.string.cd_send_message),
                    tint = colors.onAccent,
                    modifier = Modifier.size(20.dp).rotate(SEND_ICON_ROTATION_DEGREES),
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
                textStyle =
                    MaterialTheme.typography.bodyMedium.copy(
                        color = colors.inputText,
                    ),
                cursorBrush = SolidColor(colors.accent),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (value.isEmpty()) {
                            Text(
                                text = stringResource(Res.string.chat_input_hint),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.inputPlaceholder.copy(alpha = 0.7f),
                            )
                        }
                        innerTextField()
                    }
                },
            )


        }
    }
}

private suspend fun LazyListState.scrollToLastMessage(
    messageCount: Int,
    isPoetTyping: Boolean,
) {
    val lastIndex = messageCount + if (isPoetTyping) 1 else 0 - 1
    if (lastIndex >= 0) {
        animateScrollToItem(lastIndex)
    }
}

private val previewMessages =
    listOf(
        ChatMessageUi(
            id = "1",
            isFromUser = true,
            text = "دلم گرفته و خسته‌ام...",
            timeLabel = "11:30",
        ),
        ChatMessageUi(
            id = "2",
            isFromUser = false,
            text = "دلم گرفته و خسته‌ام...\n\nکه عشق آسان نمود اول",
            timeLabel = "11:30",
        ),
    )

@Preview
@Composable
private fun ChatScreenPreview() {
    AzbarkonTheme(darkTheme = false) {
        ChatScreen(
            state =
                ChatState(
                    poetName = "حافظ",
                    messages = previewMessages,
                ),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun ChatScreenDarkPreview() {
    AzbarkonTheme(darkTheme = true) {
        ChatScreen(
            state =
                ChatState(
                    poetName = "حافظ",
                    messages = previewMessages,
                ),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun ChatScreenTypingPreview() {
    AzbarkonTheme {
        ChatScreen(
            state =
                ChatState(
                    poetName = "حافظ",
                    isPoetTyping = true,
                    messages =
                        listOf(
                            ChatMessageUi(
                                id = "1",
                                isFromUser = true,
                                text = "سلام",
                                timeLabel = "11:30",
                            ),
                        ),
                ),
            onAction = {},
        )
    }
}
