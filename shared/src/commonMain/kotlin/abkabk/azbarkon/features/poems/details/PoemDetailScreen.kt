package abkabk.azbarkon.features.poems.details

import abkabk.azbarkon.core.ui.FindTextField
import abkabk.azbarkon.core.ui_base.BaseScreen
import abkabk.azbarkon.core.ui_base.LocalAzbarkonAppState
import abkabk.azbarkon.core.ui_base.ObserveAsEvents
import abkabk.azbarkon.core.ui_base.UiText
import abkabk.azbarkon.core.ui_base.asString
import abkabk.azbarkon.domain.model.PoemAudioTrack
import abkabk.azbarkon.ui.components.AzbarkonPrimaryButton
import abkabk.azbarkon.ui.components.Header
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.cd_close_find_bar
import azbarkoncmp.shared.generated.resources.close
import azbarkoncmp.shared.generated.resources.find_in_poem_hint
import azbarkoncmp.shared.generated.resources.pause_circle
import azbarkoncmp.shared.generated.resources.play_circle
import azbarkoncmp.shared.generated.resources.poem_memorize_practice
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PoemDetailRoot(
    poemId: Int,
    onBackClick: () -> Unit,
    onNavigateToTasvirNegar: (Int) -> Unit,
    onNavigateToMemorizationPractice: (Int) -> Unit,
    viewModel: PoemDetailViewModel = koinViewModel { parametersOf(poemId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()
    val appState = LocalAzbarkonAppState.current
    var snackbarMessage by remember { mutableStateOf<UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is PoemDetailEvent.ShowSnackbar -> snackbarMessage = event.message
            PoemDetailEvent.NavigateToTasvirNegar -> onNavigateToTasvirNegar(poemId)
            PoemDetailEvent.NavigateToMemorizationPractice -> onNavigateToMemorizationPractice(poemId)
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
        onRetry = { viewModel.onAction(PoemDetailAction.OnRetryClick) },
    ) {
        PoemDetailScreen(
            state = state,
            audioState = audioState,
            onAction = viewModel::onAction,
            onBackClick = onBackClick,
        )
    }
}

@Composable
fun PoemDetailScreen(
    state: PoemDetailState,
    audioState: AudioPlayerUiState,
    onAction: (PoemDetailAction) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(state.scrollToVerseId, state.verses) {
        val targetVerseId = state.scrollToVerseId ?: return@LaunchedEffect
        val targetIndex = state.verses.indexOfFirst { it.id == targetVerseId }
        if (targetIndex >= 0) {
            listState.animateScrollToItem(targetIndex)
            onAction(PoemDetailAction.OnScrollConsumed)
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Header(
            title = state.poetName,
            subtitle = state.subtitle,
            onBackClick = onBackClick,
            isBookmarked = state.isBookmarked,
            onBookmarkClick = { onAction(PoemDetailAction.OnBookmarkClick) },
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(24.dp),
        ) {
            items(
                items = state.verses,
                key = { verse -> verse.id },
            ) { verse ->
                PoemVerseItem(
                    verse = verse,
                    highlightQuery = state.highlightQuery,
                )
            }

            item {
                PoemOrnamentalDivider(
                    modifier = Modifier.padding(top = 24.dp),
                )
            }
        }

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isFindBarVisible) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    FindTextField(
                        modifier = Modifier.weight(1f),
                        value = state.findInput,
                        placeholder = stringResource(Res.string.find_in_poem_hint),
                        onValueChange = { query -> onAction(PoemDetailAction.OnFindQueryChange(query)) },
                        onSearch = {
                            keyboardController?.hide()
                            onAction(PoemDetailAction.OnFindSubmit)
                        },
                    )

                    IconButton(onClick = { onAction(PoemDetailAction.OnFindBarClose) }) {
                        Icon(
                            painter = painterResource(Res.drawable.close),
                            contentDescription = stringResource(Res.string.cd_close_find_bar),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            AudioTracksList(
                tracks = audioState.tracks,
                onPlayPauseClick = { onAction(PoemDetailAction.OnTrackPlayPauseClick(it)) },
                onSeekChanged = { track, p -> onAction(PoemDetailAction.OnTrackSeekChanged(track, p)) },
                onSeekFinished = { track, p -> onAction(PoemDetailAction.OnTrackSeekFinished(track, p)) },
            )

            PoemActionBar(
                isLiked = state.isLiked,
                onSearchClick = { onAction(PoemDetailAction.OnSearchClick) },
                onShareClick = { onAction(PoemDetailAction.OnShareClick) },
                onLikeClick = { onAction(PoemDetailAction.OnLikeClick) },
                onImageCreatorClick = { onAction(PoemDetailAction.OnImageCreatorClick) },
            )

            AzbarkonPrimaryButton(
                text = stringResource(Res.string.poem_memorize_practice),
                onClick = { onAction(PoemDetailAction.OnMemorizeClick) },
                modifier =
                    Modifier
                        .fillMaxWidth().padding(horizontal = 16.dp)
                        .height(52.dp),
            )
        }
    }
}

@Composable
fun AudioTracksList(
    tracks: List<TrackPlaybackUiState>,
    onPlayPauseClick: (PoemAudioTrack) -> Unit,
    onSeekChanged: (PoemAudioTrack, Float) -> Unit,
    onSeekFinished: (PoemAudioTrack, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        tracks.forEach { trackState ->
            TrackPlayerRow(
                state = trackState,
                onPlayPauseClick = { onPlayPauseClick(trackState.track) },
                onSeekChanged = { onSeekChanged(trackState.track, it) },
                onSeekFinished = { onSeekFinished(trackState.track, it) },
            )
        }
    }
}

@Composable
private fun TrackPlayerRow(
    state: TrackPlaybackUiState,
    onPlayPauseClick: () -> Unit,
    onSeekChanged: (Float) -> Unit,
    onSeekFinished: (Float) -> Unit,
) {
    var dragProgress by remember { mutableStateOf<Float?>(null) }
    val displayedProgress = dragProgress ?: state.progress

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(
            text = state.track.title?:"",
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 4.dp, bottom = 2.dp),
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onPlayPauseClick,
                modifier = Modifier.size(36.dp),
            ) {
                when {
                    state.isLoading -> CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                    )
                    state.isPlaying -> Icon(
                        painterResource(Res.drawable.pause_circle),
                        contentDescription = "توقف",
                        modifier = Modifier.size(20.dp),
                    )
                    else -> Icon(
                        painterResource(Res.drawable.play_circle),
                        contentDescription = "پخش",
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Slider(
                value = displayedProgress.coerceIn(0f, 1f),
                onValueChange = {
                    dragProgress = it
                    onSeekChanged(it)
                },
                onValueChangeFinished = {
                    val finalValue = dragProgress ?: state.progress
                    onSeekFinished(finalValue)
                    dragProgress = null
                },
                modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
            )

            Text(
                text = "${formatMs(state.positionMs)} / ${formatMs(state.durationMs)}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            )
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

@Preview
@Composable
private fun PoemDetailScreenPreview() {
    AzbarkonTheme {
        PoemDetailScreen(
            state =
                PoemDetailState(
                    poetName = "حافظ",
                    subtitle = "غزل شماره ۷",
                    verses =
                        listOf(
                            PoemVerseUi(
                                id = "0--1",
                                text = "شرح بیت",
                                positionType = PoemVersePositionType.Comment,
                            ),
                            PoemVerseUi(
                                id = "1-0",
                                text = "الا یا ایها الساقی ادر کاسا و ناولها",
                                positionType = PoemVersePositionType.Right,
                            ),
                            PoemVerseUi(
                                id = "1-1",
                                text = "که شد پیراهنت خونچکان من الایام",
                                positionType = PoemVersePositionType.Left,
                            ),
                            PoemVerseUi(
                                id = "2-4",
                                text = "بیت مستقل",
                                positionType = PoemVersePositionType.Single,
                            ),
                        ),
                    isLiked = true,
                ),
            audioState = AudioPlayerUiState(),
            onAction = {},
            onBackClick = {},
        )
    }
}
