package abkabk.azbarkon.features.poets

import abkabk.azbarkon.core.ui_base.BaseScreen
import abkabk.azbarkon.core.ui_base.LocalAzbarkonAppState
import abkabk.azbarkon.core.ui_base.ObserveAsEvents
import abkabk.azbarkon.core.ui_base.asString
import abkabk.azbarkon.features.poets.components.ChevronLeading
import abkabk.azbarkon.features.poets.components.PoetAvatar
import abkabk.azbarkon.features.poets.components.PoetsSectionTitle
import abkabk.azbarkon.features.poets.components.PoetsTopBar
import abkabk.azbarkon.features.poets.components.WorkCoverPlaceholder
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.poets_works_count
import azbarkoncmp.shared.generated.resources.poets_works_section
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PoetDetailRoot(
    poetId: Int,
    onBackClick: () -> Unit,
    viewModel: PoetDetailViewModel = koinViewModel { parametersOf(poetId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appState = LocalAzbarkonAppState.current
    var snackbarMessage by remember { mutableStateOf<abkabk.azbarkon.core.ui_base.UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is PoetDetailEvent.ShowSnackbar -> snackbarMessage = event.message
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
        onRetry = { viewModel.onAction(PoetDetailAction.OnRetryClick) },
    ) {
        PoetDetailScreen(
            state = state,
            onAction = viewModel::onAction,
            onBackClick = onBackClick,
        )
    }
}

@Composable
fun PoetDetailScreen(
    state: PoetDetailState,
    onAction: (PoetDetailAction) -> Unit,
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
            title = state.name,
            onBackClick = onBackClick,
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                PoetDetailHero(
                    state = state,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            item {
                PoetsSectionTitle(
                    title = stringResource(Res.string.poets_works_section),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            items(
                items = state.works,
                key = { work -> work.id },
            ) { work ->
                PoetWorkRow(
                    work = work,
                    onClick = { onAction(PoetDetailAction.OnWorkClick(work.id)) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun PoetDetailHero(
    state: PoetDetailState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(20.dp),
                ).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        PoetAvatar(
            imageUrl = state.imageUrl,
            modifier = Modifier.size(96.dp),
        )

        Text(
            text = state.name,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = state.bio,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            text = stringResource(Res.string.poets_works_count, state.works.size),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun PoetWorkRow(
    work: PoetWorkItemUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp),
                ).clickable(onClick = onClick)
                .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ChevronLeading()

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = work.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start,
            )
            if (work.subtitle.isNotBlank()) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = work.subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        WorkCoverPlaceholder(
            accentColor = work.accentColor,
            modifier = Modifier.size(width = 52.dp, height = 72.dp),
        )
    }
}

@Preview
@Composable
private fun PoetDetailScreenPreview() {
    AzbarkonTheme {
        PoetDetailScreen(
            state =
                PoetDetailState(
                    name = "حافظ شیرازی",
                    bio = "غزل‌سرای بزرگ ایران",
                    works =
                        listOf(
                            PoetWorkItemUi(
                                id = 9,
                                title = "دیوان حافظ",
                                subtitle = "غزلیات • قطعات • رباعیات",
                                accentColor = androidx.compose.ui.graphics.Color(0xFFC4A574),
                            ),
                        ),
                ),
            onAction = {},
            onBackClick = {},
        )
    }
}
