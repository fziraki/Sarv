package abkabk.azbarkon.features.search

import abkabk.azbarkon.core.ui.HighlightedText
import abkabk.azbarkon.core.uidata.BaseScreen
import abkabk.azbarkon.core.uidata.LocalAzbarkonAppState
import abkabk.azbarkon.core.uidata.ObserveAsEvents
import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.core.uidata.UiText
import abkabk.azbarkon.core.uidata.asString
import abkabk.azbarkon.ui.components.Header
import abkabk.azbarkon.ui.components.ShimmerPlaceholder
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.all
import azbarkoncmp.shared.generated.resources.list_load_error
import azbarkoncmp.shared.generated.resources.search
import azbarkoncmp.shared.generated.resources.search_choose_category
import azbarkoncmp.shared.generated.resources.search_choose_poet
import azbarkoncmp.shared.generated.resources.search_no_results
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun SearchRoot(
    initialPoetId: Int?,
    initialCatId: Int?,
    onBackClick: () -> Unit,
    onNavigateToPoemDetail: (Int) -> Unit,
    viewModelArgs: SearchNavigationArgs = SearchNavigationArgs(initialPoetId, initialCatId),
    viewModel: SearchViewModel = koinViewModel { parametersOf(viewModelArgs) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    val appState = LocalAzbarkonAppState.current
    var snackbarMessage by remember { mutableStateOf<UiText?>(null) }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is SearchEvent.NavigateToPoemDetail -> onNavigateToPoemDetail(event.poemId)
            is SearchEvent.ShowSnackbar -> snackbarMessage = event.message
        }
    }

    snackbarMessage?.let { message ->
        val resolvedMessage = message.asString()
        LaunchedEffect(resolvedMessage) {
            appState.showSnackbar(resolvedMessage)
            snackbarMessage = null
        }
    }

    val screenState =
        when {
            state.isInitializing -> UiScreenState.Loading
            searchResults.loadState.refresh is LoadState.Error && state.submittedQuery.isNotBlank() ->
                UiScreenState.Error(
                    message = UiText.Resource(Res.string.list_load_error),
                    retryable = true,
                )
            else -> UiScreenState.Success
        }

    BaseScreen(
        screenState = screenState,
        onRetry = { searchResults.retry() },
    ) {
        SearchScreen(
            state = state,
            searchResults = searchResults,
            isSearching = state.isSearching,
            onAction = viewModel::onAction,
            onBackClick = onBackClick,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchState,
    searchResults: LazyPagingItems<SearchResultUi>,
    onAction: (SearchAction) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSearching: Boolean = false
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    var showPoetPicker by remember { mutableStateOf(false) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    val allLabel = stringResource(Res.string.all)

    val selectedPoetLabel =
        state.poetOptions
            .firstOrNull { it.id == state.selectedPoetId }
            ?.let { option -> if (option.id == null) allLabel else option.name }
            ?: allLabel

    val selectedCategoryLabel =
        state.categoryOptions
            .firstOrNull { it.id == state.selectedCategoryId }
            ?.let { option -> if (option.id == null) allLabel else option.title }
            ?: allLabel

    val showNoResults =
        searchResults.loadState.refresh is LoadState.NotLoading &&
            searchResults.itemCount == 0 &&
            state.submittedQuery.isNotBlank()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Header(
            title = stringResource(Res.string.search),
            onBackClick = onBackClick,
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SearchQueryField(
                value = state.query,
                onValueChange = { onAction(SearchAction.OnQueryChange(it)) },
                onSearch = {
                    keyboardController?.hide()
                    onAction(SearchAction.OnSearchSubmit)
                },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SearchPickerField(
                    label = stringResource(Res.string.search_choose_poet),
                    value = selectedPoetLabel,
                    onClick = { showPoetPicker = true },
                    modifier = Modifier.weight(1f),
                )
                SearchPickerField(
                    label = stringResource(Res.string.search_choose_category),
                    value = selectedCategoryLabel,
                    onClick = { if (state.isCategoryPickerEnabled) showCategoryPicker = true },
                    enabled = state.isCategoryPickerEnabled,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        SearchResultsList(
            searchResults = searchResults,
            submittedQuery = state.submittedQuery,
            isSearching = isSearching,
            showNoResults = showNoResults,
            onResultClick = { poemId -> onAction(SearchAction.OnResultClick(poemId)) },
            listState = listState,
        )
    }

    SearchOptionSheets(
        state = state,
        allLabel = allLabel,
        showPoetPicker = showPoetPicker,
        showCategoryPicker = showCategoryPicker,
        onDismissPoetPicker = { showPoetPicker = false },
        onDismissCategoryPicker = { showCategoryPicker = false },
        onSelectPoet = { optionId -> onAction(SearchAction.OnPoetSelected(optionId)) },
        onSelectCategory = { optionId -> onAction(SearchAction.OnCategorySelected(optionId)) },
    )
}

@Composable
private fun SearchResultsList(
    searchResults: LazyPagingItems<SearchResultUi>,
    submittedQuery: String,
    isSearching: Boolean,
    showNoResults: Boolean,
    onResultClick: (Int) -> Unit,
    listState: LazyListState,
) {
    when {
        isSearching -> SearchResultsShimmer()

        showNoResults -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.search_no_results),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        else -> {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(
                    count = searchResults.itemCount,
                    key = searchResults.itemKey { result -> result.key },
                ) { index ->
                    searchResults[index]?.let { result ->
                        SearchResultRow(
                            result = result,
                            submittedQuery = submittedQuery,
                            onClick = { onResultClick(result.poemId) },
                        )
                    }
                }

                if (searchResults.loadState.append is LoadState.Loading) {
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

@Composable
private fun SearchResultsShimmer() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(7) {
            ShimmerPlaceholder(
                modifier =
                    Modifier.fillMaxWidth().height(64.dp)
                        .clip(RoundedCornerShape(14.dp)),
            )
        }
    }
}

@Composable
private fun SearchOptionSheets(
    state: SearchState,
    allLabel: String,
    showPoetPicker: Boolean,
    showCategoryPicker: Boolean,
    onDismissPoetPicker: () -> Unit,
    onDismissCategoryPicker: () -> Unit,
    onSelectPoet: (Int?) -> Unit,
    onSelectCategory: (Int?) -> Unit,
) {
    if (showPoetPicker) {
        SearchOptionSheet(
            title = stringResource(Res.string.search_choose_poet),
            onDismiss = onDismissPoetPicker,
        ) {
            state.poetOptions.forEach { option ->
                SearchOptionRow(
                    title = if (option.id == null) allLabel else option.name,
                    depth = 0,
                    isSelected = option.id == state.selectedPoetId,
                    onClick = {
                        onSelectPoet(option.id)
                        onDismissPoetPicker()
                    },
                )
            }
        }
    }

    if (showCategoryPicker) {
        SearchOptionSheet(
            title = stringResource(Res.string.search_choose_category),
            onDismiss = onDismissCategoryPicker,
        ) {
            state.categoryOptions.forEach { option ->
                SearchOptionRow(
                    title = if (option.id == null) allLabel else option.title,
                    depth = option.depth,
                    isSelected = option.id == state.selectedCategoryId,
                    onClick = {
                        onSelectCategory(option.id)
                        onDismissCategoryPicker()
                    },
                )
            }
        }
    }
}

@Composable
private fun SearchQueryField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(14.dp),
                ).padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(
            painter = painterResource(Res.drawable.search),
            contentDescription = stringResource(Res.string.search),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            textStyle =
                MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start,
                ),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.search),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}

@Composable
private fun SearchPickerField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (enabled) {
                        MaterialTheme.colorScheme.surfaceVariant
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                ).border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(14.dp),
                ).clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color =
                if (enabled) {
                    MaterialTheme.colorScheme.onBackground
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun SearchResultRow(
    result: SearchResultUi,
    submittedQuery: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(14.dp),
                ).clickable(onClick = onClick)
                .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = "${result.poetName} · ${result.categoryName}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = result.poemTitle,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        HighlightedText(
            text = result.verseText,
            query = submittedQuery,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchOptionSheet(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            content()
        }
    }
}

@Composable
private fun SearchOptionRow(
    title: String,
    depth: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        color =
            if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onBackground
            },
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(
                    start = (12 + depth * 16).dp,
                    top = 10.dp,
                    bottom = 10.dp,
                    end = 12.dp,
                ),
    )
}

@Preview
@Composable
private fun SearchScreenPreview() {
    AzbarkonTheme {
        SearchScreen(
            state =
                SearchState(
                    query = "زلف",
                    submittedQuery = "زلف",
                    poetOptions =
                        listOf(
                            SearchPoetOptionUi(id = null, name = "همه"),
                            SearchPoetOptionUi(id = 2, name = "حافظ شیرازی"),
                        ),
                    categoryOptions =
                        listOf(
                            SearchCategoryOptionUi(id = null, title = "همه", depth = 0),
                            SearchCategoryOptionUi(id = 24, title = "غزلیات", depth = 0),
                        ),
                    selectedPoetId = 2,
                    selectedCategoryId = 24,
                    isCategoryPickerEnabled = true,
                ),
            searchResults =
                kotlinx.coroutines.flow
                    .flowOf(
                        androidx.paging.PagingData.from(
                            listOf(
                                SearchResultUi(
                                    poemId = 1,
                                    poemTitle = "غزل شماره ۱",
                                    poetName = "حافظ",
                                    categoryName = "غزلیات",
                                    verseText = "الا یا ایها الساقی",
                                    key = "1-1",
                                ),
                            ),
                        ),
                    ).collectAsLazyPagingItems(),
            onAction = {},
            onBackClick = {},
        )
    }
}
