package abkabk.azbarkon.features.memorization.select

import abkabk.azbarkon.core.ui_base.BaseScreen
import abkabk.azbarkon.core.ui_base.ObserveAsEvents
import abkabk.azbarkon.ui.components.AzbarkonSecondaryButton
import abkabk.azbarkon.ui.components.Header
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.memorization_active_poems
import azbarkoncmp.shared.generated.resources.memorization_quick_start
import azbarkoncmp.shared.generated.resources.memorization_quick_start_baba_taher
import azbarkoncmp.shared.generated.resources.memorization_quick_start_hafez
import azbarkoncmp.shared.generated.resources.memorization_quick_start_treasury
import azbarkoncmp.shared.generated.resources.memorization_select_title
import azbarkoncmp.shared.generated.resources.search
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MemorizationSelectRoot(
    onBackClick: () -> Unit,
    onNavigateToPoetDetail: (Int) -> Unit,
    onNavigateToPoemList: (Int, String) -> Unit,
    onNavigateToTreasury: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToActivePoems: () -> Unit,
    viewModel: MemorizationSelectViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            MemorizationSelectEvent.NavigateBack -> onBackClick()
            is MemorizationSelectEvent.NavigateToPoetDetail -> onNavigateToPoetDetail(event.poetId)
            is MemorizationSelectEvent.NavigateToPoemList ->
                onNavigateToPoemList(event.catId, event.title)
            MemorizationSelectEvent.NavigateToTreasury -> onNavigateToTreasury()
            MemorizationSelectEvent.NavigateToSearch -> onNavigateToSearch()
            MemorizationSelectEvent.NavigateToActivePoems -> onNavigateToActivePoems()
            is MemorizationSelectEvent.ShowSnackbar -> Unit
        }
    }

    BaseScreen(
        screenState = state.screenState,
        onRetry = { viewModel.onAction(MemorizationSelectAction.OnLoad) },
    ) {
        MemorizationSelectScreen(onAction = viewModel::onAction)
    }
}

@Composable
fun MemorizationSelectScreen(
    onAction: (MemorizationSelectAction) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Header(
            title = stringResource(Res.string.memorization_select_title),
            onBackClick = { onAction(MemorizationSelectAction.OnBackClick) },
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(Res.string.memorization_quick_start),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            QuickStartItem(
                text = stringResource(Res.string.memorization_quick_start_baba_taher),
                onClick = { onAction(MemorizationSelectAction.OnBabaTaherClick) },
            )
            QuickStartItem(
                text = stringResource(Res.string.memorization_quick_start_hafez),
                onClick = { onAction(MemorizationSelectAction.OnHafezGhazalsClick) },
            )
            QuickStartItem(
                text = stringResource(Res.string.memorization_quick_start_treasury),
                onClick = { onAction(MemorizationSelectAction.OnTreasuryClick) },
            )

            QuickStartItem(
                text = stringResource(Res.string.search),
                onClick = { onAction(MemorizationSelectAction.OnSearchClick) },
            )
            QuickStartItem(
                text = stringResource(Res.string.memorization_active_poems),
                onClick = { onAction(MemorizationSelectAction.OnActivePoemsClick) },
            )
        }
    }
}

@Composable
private fun QuickStartItem(
    text: String,
    onClick: () -> Unit,
) {
    AzbarkonSecondaryButton(
        text = text,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Preview
@Composable
private fun MemorizationSelectScreenPreview() {
    AzbarkonTheme {
        MemorizationSelectScreen(onAction = {})
    }
}
