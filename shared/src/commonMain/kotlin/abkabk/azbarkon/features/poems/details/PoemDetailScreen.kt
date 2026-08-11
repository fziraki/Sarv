package abkabk.azbarkon.features.poems.details

import abkabk.azbarkon.core.ui.FindTextField
import abkabk.azbarkon.core.ui.keyboardAboveIme
import abkabk.azbarkon.core.uidata.BaseScreen
import abkabk.azbarkon.core.uidata.LocalAzbarkonAppState
import abkabk.azbarkon.core.uidata.ObserveAsEvents
import abkabk.azbarkon.core.uidata.UiText
import abkabk.azbarkon.core.uidata.asString
import abkabk.azbarkon.domain.model.PoemAudioTrack
import abkabk.azbarkon.ui.components.Header
import abkabk.azbarkon.ui.components.HeaderAction
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import abkabk.azbarkon.ui.theme.LightColorScheme
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.arrow_drop_down
import azbarkoncmp.shared.generated.resources.cd_close_find_bar
import azbarkoncmp.shared.generated.resources.cd_select_track
import azbarkoncmp.shared.generated.resources.close
import azbarkoncmp.shared.generated.resources.find_in_poem_hint
import azbarkoncmp.shared.generated.resources.pause
import azbarkoncmp.shared.generated.resources.play
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
    val focusManager = LocalFocusManager.current
    val findFocusRequester = remember { FocusRequester() }
    val currentOnAction by rememberUpdatedState(onAction)

    LaunchedEffect(state.isFindBarVisible) {
        if (state.isFindBarVisible) {
            findFocusRequester.requestFocus()
        } else {
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    }

    LaunchedEffect(state.scrollToVerseId, state.verses) {
        val targetVerseId = state.scrollToVerseId ?: return@LaunchedEffect
        val targetIndex = state.verses.indexOfFirst { it.id == targetVerseId }
        if (targetIndex >= 0) {
            listState.animateScrollToItem(targetIndex)
            currentOnAction(PoemDetailAction.OnScrollConsumed)
        }
    }

    Scaffold(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        topBar = {
            Column {
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
            }
        },
        bottomBar = {
            Column(
                modifier =
                    Modifier
                        .keyboardAboveIme()
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (state.isFindBarVisible) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        FindTextField(
                            modifier = Modifier.weight(1f).focusRequester(findFocusRequester),
                            value = state.findInput,
                            placeholder = stringResource(Res.string.find_in_poem_hint),
                            onValueChange = { query -> onAction(PoemDetailAction.OnFindQueryChange(query)) },
                            onSearch = {
                                onAction(PoemDetailAction.OnFindSubmit)
                            },
                        )

                        IconButton(onClick = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            onAction(PoemDetailAction.OnFindBarClose)
                        }) {
                            Icon(
                                painter = painterResource(Res.drawable.close),
                                contentDescription = stringResource(Res.string.cd_close_find_bar),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }else{

                    TrackPlayerCard(
                        tracks = audioState.tracks,
                        activeTrackUrl = audioState.activeTrackUrl,
                        onPlayPauseClick = { onAction(PoemDetailAction.OnTrackPlayPauseClick(it)) },
                        onSelectTrack = { onAction(PoemDetailAction.OnTrackSelect(it)) },
                        onSeekChange = { track, p -> onAction(PoemDetailAction.OnTrackSeekChanged(track, p)) },
                        onSeekFinish = { track, p -> onAction(PoemDetailAction.OnTrackSeekFinished(track, p)) },
                    )

                    PoemActionBar(
                        isLiked = state.isLiked,
                        onSearchClick = { onAction(PoemDetailAction.OnSearchClick) },
                        onShareClick = { onAction(PoemDetailAction.OnShareClick) },
                        onLikeClick = { onAction(PoemDetailAction.OnLikeClick) },
                        onImageCreatorClick = { onAction(PoemDetailAction.OnImageCreatorClick) },
                        onMemorizeClick = { onAction(PoemDetailAction.OnMemorizeClick) },
                    )
                }

            }
        }
    ) { paddingValues ->

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    bottom = paddingValues.calculateBottomPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                ),
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

    }
}

@Composable
private fun TrackSelector(
    tracks: List<TrackPlaybackUiState>,
    selected: TrackPlaybackUiState,
    onSelectTrack: (PoemAudioTrack) -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { menuExpanded = true },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = trackLabel(selected.track),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).padding(start = 8.dp),
            )
            Icon(
                painter = painterResource(Res.drawable.arrow_drop_down),
                contentDescription = stringResource(Res.string.cd_select_track),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp),
            )
        }

        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false },
        ) {
            tracks.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = trackLabel(item.track),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onSelectTrack(item.track)
                    },
                )
            }
        }
    }
}

private fun trackLabel(track: PoemAudioTrack): String {
    val artist = track.artist?.takeIf { it.isNotBlank() }
    val title = track.title?.takeIf { it.isNotBlank() }
    return when {
        artist != null && title != null -> "$artist • $title"
        artist != null -> artist
        title != null -> title
        else -> ""
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrackPlayerCard(
    tracks: List<TrackPlaybackUiState>,
    activeTrackUrl: String?,
    onPlayPauseClick: (PoemAudioTrack) -> Unit,
    onSelectTrack: (PoemAudioTrack) -> Unit,
    onSeekChange: (PoemAudioTrack, Float) -> Unit,
    onSeekFinish: (PoemAudioTrack, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val activeTrack = tracks.firstOrNull { it.track.url == activeTrackUrl } ?: return
    var dragProgress by remember { mutableStateOf<Float?>(null) }
    val displayedProgress = dragProgress ?: activeTrack.progress

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp),
            ).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        PlayPauseButton(
            isPlaying = activeTrack.isPlaying,
            isLoading = activeTrack.isLoading,
            onClick = { onPlayPauseClick(activeTrack.track) },
        )


        Column(modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {

            TrackSelector(
                tracks = tracks,
                selected = activeTrack,
                onSelectTrack = onSelectTrack,
            )

            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Slider(
                    value = displayedProgress.coerceIn(0f, 1f),
                    onValueChange = {
                        dragProgress = it
                        onSeekChange(activeTrack.track, it)
                    },
                    onValueChangeFinished = {
                        val finalValue = dragProgress ?: activeTrack.progress
                        onSeekFinish(activeTrack.track, finalValue)
                        dragProgress = null
                    },
                    modifier = Modifier.weight(1f).height(16.dp),
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
                                .background(LightColorScheme.outlineVariant),
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

                Text(
                    modifier = modifier.padding(end = 6.dp),
                    text = "${formatMs(activeTrack.positionMs)} / ${formatMs(activeTrack.durationMs)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }


    }


}

@Composable
private fun PlayPauseButton(
    isPlaying: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
) {

    val iconTint = MaterialTheme.colorScheme.tertiary

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(enabled = !isLoading, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(if (isPlaying) Res.drawable.pause else Res.drawable.play),
                contentDescription = if (isPlaying) "توقف" else "پخش",
                tint = iconTint,
                modifier = Modifier.size(20.dp),
            )
        }

        if (isLoading) {

            val ringProgress = remember { Animatable(0f) }

            LaunchedEffect(Unit) {
                ringProgress.snapTo(0f)
                ringProgress.animateTo(1f, animationSpec = tween(3000))
            }
            CircularProgressIndicator(
                progress = { ringProgress.value },
                modifier = Modifier.size(48.dp),
                strokeWidth = 3.dp,
                color = iconTint,
                trackColor = MaterialTheme.colorScheme.primary,
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

