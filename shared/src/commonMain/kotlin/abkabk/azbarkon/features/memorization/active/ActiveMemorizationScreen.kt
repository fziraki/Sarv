package abkabk.azbarkon.features.memorization.active

import abkabk.azbarkon.core.ui_base.BaseScreen
import abkabk.azbarkon.core.ui_base.LocalAzbarkonAppState
import abkabk.azbarkon.core.ui_base.ObserveAsEvents
import abkabk.azbarkon.core.ui_base.asString
import abkabk.azbarkon.ui.components.Header
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.clear_cancel
import azbarkoncmp.shared.generated.resources.clear_confirm
import azbarkoncmp.shared.generated.resources.ic_delete
import azbarkoncmp.shared.generated.resources.memorization_active_empty
import azbarkoncmp.shared.generated.resources.memorization_active_poems
import azbarkoncmp.shared.generated.resources.memorization_due_cards_format
import azbarkoncmp.shared.generated.resources.memorization_remove_confirm_body
import azbarkoncmp.shared.generated.resources.memorization_remove_confirm_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ActiveMemorizationRoot(
    onBackClick: () -> Unit,
    onNavigateToPractice: (Int) -> Unit,
    viewModel: ActiveMemorizationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appState = LocalAzbarkonAppState.current
    var snackbarMessage by remember { mutableStateOf<abkabk.azbarkon.core.ui_base.UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ActiveMemorizationEvent.NavigateBack -> onBackClick()
            is ActiveMemorizationEvent.NavigateToPractice -> onNavigateToPractice(event.poemId)
            is ActiveMemorizationEvent.ShowSnackbar -> snackbarMessage = event.message
        }
    }

    snackbarMessage?.let { message ->
        val resolvedMessage = message.asString()
        LaunchedEffect(resolvedMessage) {
            appState.showSnackbar(resolvedMessage)
            snackbarMessage = null
        }
    }

    if (state.poemToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.onAction(ActiveMemorizationAction.OnDeleteDismiss) },
            title = { Text(stringResource(Res.string.memorization_remove_confirm_title)) },
            text = { Text(stringResource(Res.string.memorization_remove_confirm_body)) },
            confirmButton = {
                TextButton(onClick = { viewModel.onAction(ActiveMemorizationAction.OnDeleteConfirm) }) {
                    Text(stringResource(Res.string.clear_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onAction(ActiveMemorizationAction.OnDeleteDismiss) }) {
                    Text(stringResource(Res.string.clear_cancel))
                }
            },
        )
    }

    BaseScreen(
        screenState = state.screenState,
        onRetry = { viewModel.onAction(ActiveMemorizationAction.OnRetryClick) },
    ) {
        ActiveMemorizationScreen(
            state = state,
            onAction = viewModel::onAction,
        )
    }
}

@Composable
fun ActiveMemorizationScreen(
    state: ActiveMemorizationState,
    onAction: (ActiveMemorizationAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Header(
            title = stringResource(Res.string.memorization_active_poems),
            onBackClick = { onAction(ActiveMemorizationAction.OnBackClick) },
        )

        if (state.poems.isEmpty()) {
            Text(
                text = stringResource(Res.string.memorization_active_empty),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(24.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.poems, key = { it.poemId }) { poem ->
                    ActivePoemRow(poem = poem, onAction = onAction)
                }
            }
        }
    }
}

@Composable
private fun ActivePoemRow(
    poem: ActiveMemorizationPoemUi,
    onAction: (ActiveMemorizationAction) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onAction(ActiveMemorizationAction.OnPoemClick(poem.poemId)) }
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = poem.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = poem.poetName,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = poem.statusLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
            LinearProgressIndicator(
                progress = { poem.progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
            )
            if (poem.dueCards > 0) {
                Text(
                    text =
                        stringResource(
                            Res.string.memorization_due_cards_format,
                            poem.dueCards,
                        ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        IconButton(onClick = { onAction(ActiveMemorizationAction.OnDeleteClick(poem.poemId)) }) {
            Icon(
                painter = painterResource(Res.drawable.ic_delete),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Preview
@Composable
private fun ActiveMemorizationScreenPreview() {
    AzbarkonTheme {
        ActiveMemorizationScreen(
            state =
                ActiveMemorizationState(
                    poems =
                        listOf(
                            ActiveMemorizationPoemUi(
                                poemId = 1,
                                title = "غزل ۱",
                                poetName = "حافظ",
                                statusLabel = "Box 2 / Level 2",
                                progress = 0.4f,
                                dueCards = 3,
                            ),
                        ),
                ),
            onAction = {},
        )
    }
}
