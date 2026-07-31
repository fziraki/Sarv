package abkabk.azbarkon.features.poems.details

import abkabk.azbarkon.core.designsystem.secondary
import abkabk.azbarkon.core.designsystem.secondaryFixed
import abkabk.azbarkon.core.ui.FindTextField
import abkabk.azbarkon.core.uidata.BaseScreen
import abkabk.azbarkon.core.uidata.LocalAzbarkonAppState
import abkabk.azbarkon.core.uidata.ObserveAsEvents
import abkabk.azbarkon.core.uidata.UiText
import abkabk.azbarkon.core.uidata.asString
import abkabk.azbarkon.domain.model.PoemAudioTrack
import abkabk.azbarkon.ui.components.AzbarkonPrimaryButton
import abkabk.azbarkon.ui.components.Header
import abkabk.azbarkon.ui.components.HeaderAction
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.cd_close_find_bar
import azbarkoncmp.shared.generated.resources.close
import azbarkoncmp.shared.generated.resources.find_in_poem_hint
import azbarkoncmp.shared.generated.resources.pause
import azbarkoncmp.shared.generated.resources.play
import azbarkoncmp.shared.generated.resources.poem_memorize_practice
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val TRACK_MIN_DIVISOR = 0.001f
private const val MILLIS_PER_SECOND = 1000L
private const val SECONDS_PER_MINUTE = 60

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
    val currentOnAction by rememberUpdatedState(onAction)

    LaunchedEffect(state.scrollToVerseId, state.verses) {
        val targetVerseId = state.scrollToVerseId ?: return@LaunchedEffect
        val targetIndex = state.verses.indexOfFirst { it.id == targetVerseId }
        if (targetIndex >= 0) {
            listState.animateScrollToItem(targetIndex)
            currentOnAction(PoemDetailAction.OnScrollConsumed)
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
            action =
                HeaderAction.Bookmark(isBookmarked = state.isBookmarked) {
                    onAction(PoemDetailAction.OnBookmarkClick)
                },
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
                onSeekChange = { track, p -> onAction(PoemDetailAction.OnTrackSeekChanged(track, p)) },
                onSeekFinish = { track, p -> onAction(PoemDetailAction.OnTrackSeekFinished(track, p)) },
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
    onSeekChange: (PoemAudioTrack, Float) -> Unit,
    onSeekFinish: (PoemAudioTrack, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        tracks.forEach { trackState ->
            TrackPlayerCard(
                state = trackState,
                onPlayPauseClick = { onPlayPauseClick(trackState.track) },
                onSeekChange = { onSeekChange(trackState.track, it) },
                onSeekFinish = { onSeekFinish(trackState.track, it) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackPlayerCard(
    state: TrackPlaybackUiState,
    onPlayPauseClick: () -> Unit,
    onSeekChange: (Float) -> Unit,
    onSeekFinish: (Float) -> Unit,
) {
    var dragProgress by remember { mutableStateOf<Float?>(null) }
    val displayedProgress = dragProgress ?: state.progress
    val isActive = state.isPlaying || state.isLoading

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PlayPauseButton(
            isPlaying = state.isPlaying,
            isLoading = state.isLoading,
            isActive = isActive,
            onClick = onPlayPauseClick,
        )
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.track.title ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${formatMs(state.positionMs)} / ${formatMs(state.durationMs)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Slider(
                value = displayedProgress.coerceIn(0f, 1f),
                onValueChange = {
                    dragProgress = it
                    onSeekChange(it)
                },
                onValueChangeFinished = {
                    val finalValue = dragProgress ?: state.progress
                    onSeekFinish(finalValue)
                    dragProgress = null
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Transparent,
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent,
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape,
                            ),
                    )
                },
                track = { sliderState ->
                    val trackProgress =
                        (sliderState.value - sliderState.valueRange.start) /
                                (sliderState.valueRange.endInclusive - sliderState.valueRange.start).coerceAtLeast(TRACK_MIN_DIVISOR)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                    ) {
                        if (trackProgress > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(trackProgress)
                                    .background(MaterialTheme.colorScheme.primary),
                            )
                        }
                        if (trackProgress < 1f) {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f - trackProgress),
                            )
                        }
                    }
                },
            )
        }
    }

}

@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    isLoading: Boolean,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    val backgroundColor = if (isActive) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest
    }
    val iconTint = if (isActive) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        when {
            isLoading -> CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = iconTint,
            )
            isPlaying -> Icon(
                painter = painterResource(Res.drawable.pause),
                contentDescription = "توقف",
                tint = iconTint,
                modifier = Modifier.size(20.dp),
            )
            else -> Icon(
                painter = painterResource(Res.drawable.play),
                contentDescription = "پخش",
                tint = iconTint,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

private fun formatMs(ms: Long): String {
    val totalSeconds = ms / MILLIS_PER_SECOND
    val minutes = totalSeconds / SECONDS_PER_MINUTE
    val seconds = totalSeconds % SECONDS_PER_MINUTE
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
