package abkabk.azbarkon.features.poems.details

import abkabk.azbarkon.core.ui_base.BaseScreen
import abkabk.azbarkon.core.ui_base.LocalAzbarkonAppState
import abkabk.azbarkon.core.ui_base.ObserveAsEvents
import abkabk.azbarkon.core.ui_base.UiText
import abkabk.azbarkon.core.ui_base.asString
import abkabk.azbarkon.features.poets.list.PoetsTopBar
import abkabk.azbarkon.ui.components.AzbarkonPrimaryButton
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.poem_memorize_practice
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PoemDetailRoot(
    poemId: Int,
    onBackClick: () -> Unit,
    viewModel: PoemDetailViewModel = koinViewModel { parametersOf(poemId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appState = LocalAzbarkonAppState.current
    var snackbarMessage by remember { mutableStateOf<UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is PoemDetailEvent.ShowSnackbar -> snackbarMessage = event.message
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
            onAction = viewModel::onAction,
            onBackClick = onBackClick,
        )
    }
}

@Composable
fun PoemDetailScreen(
    state: PoemDetailState,
    onAction: (PoemDetailAction) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        PoetsTopBar(
            title = state.poetName,
            subtitle = state.subtitle,
            onBackClick = onBackClick,
            isBookmarked = state.isBookmarked,
            onBookmarkClick = { onAction(PoemDetailAction.OnBookmarkClick) },
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(24.dp),
        ) {
            items(
                items = state.verses,
                key = { verse -> verse.id },
            ) { verse ->
                PoemVerseItem(verse = verse)
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
            PoemActionBar(
                isLiked = state.isLiked,
                onCopyClick = { onAction(PoemDetailAction.OnCopyClick) },
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
            onAction = {},
            onBackClick = {},
        )
    }
}
