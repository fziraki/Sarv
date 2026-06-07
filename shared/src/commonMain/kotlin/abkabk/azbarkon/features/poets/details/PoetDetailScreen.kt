package abkabk.azbarkon.features.poets.details

import abkabk.azbarkon.core.ui_base.BaseScreen
import abkabk.azbarkon.core.ui_base.LocalAzbarkonAppState
import abkabk.azbarkon.core.ui_base.ObserveAsEvents
import abkabk.azbarkon.core.ui_base.UiText
import abkabk.azbarkon.core.ui_base.asString
import abkabk.azbarkon.features.poets.PoetCategoryRowUi
import abkabk.azbarkon.features.poets.list.PoetAvatar
import abkabk.azbarkon.features.poets.list.PoetsSectionTitle
import abkabk.azbarkon.features.poets.list.PoetsTopBar
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import azbarkoncmp.shared.generated.resources.poets_works_section
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PoetDetailRoot(
    poetId: Int,
    onBackClick: () -> Unit,
    onNavigateToPoemList: (catId: Int, title: String) -> Unit,
    viewModel: PoetDetailViewModel = koinViewModel { parametersOf(poetId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appState = LocalAzbarkonAppState.current
    var snackbarMessage by remember { mutableStateOf<UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is PoetDetailEvent.NavigateToPoemList -> {
                onNavigateToPoemList(event.catId, event.title)
            }

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
        ) {
            item {
                PoetDetailHero(
                    state = state,
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                )
            }

            item {
                PoetsSectionTitle(
                    title = stringResource(Res.string.poets_works_section),
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                )
            }

            itemsIndexed(
                items = state.categories,
                key = { _, category -> "${category.id}-${category.depth}" },
            ) { index, category ->
                PoetCategoryRow(
                    category = category,
                    onToggleClick = { onAction(PoetDetailAction.OnCategoryToggle(category.id)) },
                    onLeafClick = {
                        onAction(
                            PoetDetailAction.OnCategoryClick(
                                categoryId = category.id,
                                title = category.title,
                            ),
                        )
                    },
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp)
                            .then(
                                if (index > 0) {
                                    Modifier.padding(top = 6.dp)
                                } else {
                                    Modifier
                                },
                            ),
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

        if (state.bio.isNotBlank()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = state.bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
        }
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
                    categories =
                        listOf(
                            PoetCategoryRowUi(
                                id = 24,
                                title = "غزلیات",
                                depth = 0,
                                isParent = true,
                                isExpanded = false,
                            ),
                            PoetCategoryRowUi(
                                id = 25,
                                title = "قطعات",
                                depth = 0,
                                isParent = false,
                                isExpanded = false,
                            ),
                        ),
                ),
            onAction = {},
            onBackClick = {},
        )
    }
}
