package abkabk.azbarkon.features.poems.list

import abkabk.azbarkon.core.ui_base.LocalAzbarkonAppState
import abkabk.azbarkon.core.ui_base.ObserveAsEvents
import abkabk.azbarkon.core.ui_base.UiText
import abkabk.azbarkon.core.ui_base.asString
import abkabk.azbarkon.ui.components.Header
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.list_load_error
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PoemListRoot(
    catId: Int,
    title: String,
    onBackClick: () -> Unit,
    onNavigateToPoemDetail: (Int) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: PoemListViewModel = koinViewModel { parametersOf(catId, title) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val poems = viewModel.poems.collectAsLazyPagingItems()
    val appState = LocalAzbarkonAppState.current
    var snackbarMessage by remember { mutableStateOf<UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is PoemListEvent.ShowSnackbar -> snackbarMessage = event.message
            is PoemListEvent.NavigateToPoemDetail -> onNavigateToPoemDetail(event.poemId)
        }
    }

    snackbarMessage?.let { message ->
        val resolvedMessage = message.asString()
        LaunchedEffect(resolvedMessage) {
            appState.showSnackbar(resolvedMessage)
            snackbarMessage = null
        }
    }

    PoemListScreen(
        state = state,
        poems = poems,
        onBackClick = onBackClick,
        onPoemClick = { poemId -> viewModel.onAction(PoemListAction.OnPoemClick(poemId)) },
        onSearchClick = onNavigateToSearch,
    )
}

@Composable
fun PoemListScreen(
    state: PoemListState,
    poems: LazyPagingItems<PoemListItemUi>,
    onBackClick: () -> Unit,
    onPoemClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isInitialLoading = poems.loadState.refresh is LoadState.Loading && poems.itemCount == 0
    val hasRefreshError = poems.loadState.refresh is LoadState.Error

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Header(
            title = state.title,
            onBackClick = onBackClick,
            onSearchClick = onSearchClick,
        )

        when {
            isInitialLoading -> {
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            hasRefreshError -> {
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clickable { poems.retry() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(Res.string.list_load_error),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp),
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(
                        count = poems.itemCount,
                        key = poems.itemKey { poem -> poem.id },
                    ) { index ->
                        poems[index]?.let { poem ->
                            Text(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { onPoemClick(poem.id) }
                                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant,
                                            shape = RoundedCornerShape(16.dp),
                                        ).padding(14.dp),
                                text = poem.title,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Start,
                            )
                        }
                    }

                    if (poems.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PoemListScreenPreview() {
    AzbarkonTheme {
        PoemListScreen(
            state = PoemListState(title = "غزلیات"),
            poems =
                flowOf(
                    PagingData.from(
                        listOf(
                            PoemListItemUi(id = 1, title = "شمارهٔ ۱"),
                            PoemListItemUi(id = 2, title = "شمارهٔ ۲"),
                        ),
                    ),
                ).collectAsLazyPagingItems(),
            onBackClick = {},
            onPoemClick = {},
            onSearchClick = {},
        )
    }
}
