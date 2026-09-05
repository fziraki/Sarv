package abkabk.azbarkon.features.memorization.active

import abkabk.azbarkon.core.uidata.BaseScreen
import abkabk.azbarkon.core.uidata.ObserveAsEvents
import abkabk.azbarkon.features.memorization.ActivePoemCard
import abkabk.azbarkon.features.memorization.MemorizationHeroSection
import abkabk.azbarkon.features.memorization.MemorizationOptionRow
import abkabk.azbarkon.ui.components.AnimatedTabRow
import abkabk.azbarkon.ui.components.SarvAlertDialog
import abkabk.azbarkon.ui.components.SarvPrimaryButton
import abkabk.azbarkon.ui.components.Header
import abkabk.azbarkon.ui.theme.SarvTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sarv.shared.generated.resources.Res
import sarv.shared.generated.resources.add_box_24px
import sarv.shared.generated.resources.clear_cancel
import sarv.shared.generated.resources.clear_confirm
import sarv.shared.generated.resources.memorization_active_add_poem
import sarv.shared.generated.resources.memorization_active_empty
import sarv.shared.generated.resources.memorization_active_poems
import sarv.shared.generated.resources.memorization_completed_empty
import sarv.shared.generated.resources.memorization_completed_poems
import sarv.shared.generated.resources.memorization_remove_confirm_body
import sarv.shared.generated.resources.memorization_remove_confirm_title
import sarv.shared.generated.resources.memorization_select_hero_subtitle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import abkabk.azbarkon.core.designsystem.LocalSarvDimensions

private const val MAX_ACTIVE_POEMS = 3

@Composable
fun ActiveMemorizationRoot(
    onBackClick: () -> Unit,
    onNavigateToPractice: (Int) -> Unit,
    onNavigateToSelect: () -> Unit,
    viewModel: ActiveMemorizationViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onAction(ActiveMemorizationAction.OnResume)
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            ActiveMemorizationEvent.NavigateBack -> onBackClick()
            ActiveMemorizationEvent.NavigateToSelect -> onNavigateToSelect()
            is ActiveMemorizationEvent.NavigateToPractice -> onNavigateToPractice(event.poemId)
        }
    }

    if (state.poemToDelete != null) {
        SarvAlertDialog(
            onDismissRequest = { viewModel.onAction(ActiveMemorizationAction.OnDeleteDismiss) },
            title = stringResource(Res.string.memorization_remove_confirm_title),
            text = stringResource(Res.string.memorization_remove_confirm_body),
            confirmLabel = stringResource(Res.string.clear_confirm),
            onConfirm = { viewModel.onAction(ActiveMemorizationAction.OnDeleteConfirm) },
            dismissLabel = stringResource(Res.string.clear_cancel),
        )
    }

    BaseScreen(
        screenState = state.screenState,
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
    modifier: Modifier = Modifier,
) {
    val tabs = MemorizationTab.entries
    val tabTitles = tabs.map { tab ->
        when (tab) {
            MemorizationTab.ACTIVE -> stringResource(Res.string.memorization_active_poems)
            MemorizationTab.COMPLETED -> stringResource(Res.string.memorization_completed_poems)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Header(
            title = stringResource(Res.string.memorization_active_poems),
            onBackClick = { onAction(ActiveMemorizationAction.OnBackClick) },
        )

        AnimatedTabRow(
            selectedTab = state.selectedTab,
            onSelectTab = { tab -> onAction(ActiveMemorizationAction.OnTabSelected(tab)) },
            tabTitles = tabTitles,
            tabs = tabs,
        )

        when (state.selectedTab) {
            MemorizationTab.ACTIVE -> {
                if (state.poems.isEmpty()) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(LocalSarvDimensions.current.dimen24),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(LocalSarvDimensions.current.dimen24, Alignment.CenterVertically),
                    ) {
                        MemorizationHeroSection()
                        Text(
                            text = stringResource(Res.string.memorization_active_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        SarvPrimaryButton(
                            text = stringResource(Res.string.memorization_active_add_poem),
                            onClick = { onAction(ActiveMemorizationAction.OnAddPoemClick) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(LocalSarvDimensions.current.dimen16),
                        verticalArrangement = Arrangement.spacedBy(LocalSarvDimensions.current.dimen12),
                    ) {
                        items(state.poems, key = { it.poemId }) { poem ->
                            ActivePoemCard(
                                title = poem.title,
                                poetName = poem.poetName,
                                reviewCount = poem.reviewCount,
                                nextReviewDays = poem.nextReviewDays,
                                isCompleted = poem.isCompleted,
                                onClick = { onAction(ActiveMemorizationAction.OnPoemClick(poem.poemId)) },
                                onDeleteClick = { onAction(ActiveMemorizationAction.OnDeleteClick(poem.poemId)) },
                                onReReviewClick = { },
                                totalCards = poem.totalCards,
                                reviewedCards = poem.reviewedCards,
                            )
                        }

                        if (state.poems.size < MAX_ACTIVE_POEMS) {
                            item(key = "add_poem") {
                                MemorizationOptionRow(
                                    title = stringResource(Res.string.memorization_active_add_poem),
                                    description = stringResource(Res.string.memorization_select_hero_subtitle),
                                    icon = Res.drawable.add_box_24px,
                                    onClick = { onAction(ActiveMemorizationAction.OnAddPoemClick) },
                                )
                            }
                        }
                    }
                }
            }

            MemorizationTab.COMPLETED -> {
                if (state.completedPoems.isEmpty()) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(LocalSarvDimensions.current.dimen24),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(LocalSarvDimensions.current.dimen24, Alignment.CenterVertically),
                    ) {
                        Text(
                            text = stringResource(Res.string.memorization_completed_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(LocalSarvDimensions.current.dimen16),
                        verticalArrangement = Arrangement.spacedBy(LocalSarvDimensions.current.dimen12),
                    ) {
                        items(state.completedPoems, key = { it.poemId }) { poem ->
                            ActivePoemCard(
                                title = poem.title,
                                poetName = poem.poetName,
                                reviewCount = poem.reviewCount,
                                nextReviewDays = poem.nextReviewDays,
                                isCompleted = poem.isCompleted,
                                onClick = { onAction(ActiveMemorizationAction.OnPoemClick(poem.poemId)) },
                                onDeleteClick = { },
                                onReReviewClick = { onAction(ActiveMemorizationAction.OnReReviewClick(poem.poemId)) },
                                totalCards = poem.totalCards,
                                reviewedCards = poem.reviewedCards,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ActiveMemorizationScreenPreview() {
    SarvTheme {
        ActiveMemorizationScreen(
            state =
                ActiveMemorizationState(
                    poems =
                        listOf(
                            ActiveMemorizationPoemUi(
                                poemId = 1,
                                title = "غزل ۱",
                                poetName = "حافظ",
                                reviewCount = 5,
                                nextReviewDays = 3,
                                isCompleted = false,
                                totalCards = 10,
                                reviewedCards = 4,
                            ),
                        ),
                ),
            onAction = {},
        )
    }
}

@Preview
@Composable
private fun ActiveMemorizationEmptyScreenPreview() {
    SarvTheme {
        ActiveMemorizationScreen(
            state = ActiveMemorizationState(),
            onAction = {},
        )
    }
}
