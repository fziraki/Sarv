package abkabk.azbarkon.features.poets.details

import abkabk.azbarkon.core.uidata.BaseScreen
import abkabk.azbarkon.core.uidata.LocalAzbarkonAppState
import abkabk.azbarkon.core.uidata.ObserveAsEvents
import abkabk.azbarkon.core.uidata.UiText
import abkabk.azbarkon.core.uidata.asString
import abkabk.azbarkon.features.poets.PoetCategoryRowUi
import abkabk.azbarkon.features.poets.list.PoetAvatar
import abkabk.azbarkon.features.poets.list.PoetsSectionTitle
import abkabk.azbarkon.ui.components.Header
import abkabk.azbarkon.ui.components.HeaderAction
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.chat_bubble
import azbarkoncmp.shared.generated.resources.chat_with_poet
import azbarkoncmp.shared.generated.resources.cd_chat
import azbarkoncmp.shared.generated.resources.poet_bio_read_more
import azbarkoncmp.shared.generated.resources.poets_works_section
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PoetDetailRoot(
    poetId: Int,
    onBackClick: () -> Unit,
    onNavigateToPoemList: (catId: Int, title: String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToChat: () -> Unit,
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

            is PoetDetailEvent.NavigateToChat -> onNavigateToChat()

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
            onSearchClick = onNavigateToSearch
        )
    }
}

@Composable
fun PoetDetailScreen(
    state: PoetDetailState,
    onAction: (PoetDetailAction) -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Header(
            title = state.name,
            onBackClick = onBackClick,
            action = HeaderAction.Search(onSearchClick),
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                PoetDetailHero(
                    state = state,
                    onAction = onAction,
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
    onAction: (PoetDetailAction) -> Unit,
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

        if (state.canChat) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onAction(PoetDetailAction.OnChatClick) }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = stringResource(Res.string.chat_with_poet),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                Icon(
                    painter = painterResource(Res.drawable.chat_bubble),
                    contentDescription = stringResource(Res.string.cd_chat),
                    tint = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }

        if (state.bio.isNotBlank()) {
            PoetBioText(
                bio = state.bio,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun PoetBioText(
    bio: String,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isOverflowing by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = bio,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = if (isExpanded) Int.MAX_VALUE else 4,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { layoutResult ->
                if (!isExpanded) {
                    isOverflowing = layoutResult.hasVisualOverflow
                }
            },
        )

        if (isOverflowing && !isExpanded) {
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = true },
                text = stringResource(Res.string.poet_bio_read_more),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primaryContainer,
                textAlign = TextAlign.Center,
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
                    bio =
                        "خواجه شمس‌الدین محمد بن بهاءالدین حافظ شیرازی، " +
                            "شاعر بزرگ ایرانی و غزل‌سرای نامدار سدهٔ هفتم هجری است. " +
                            "دیوان اشعار او از مهم‌ترین آثار ادبی فارسی به‌شمار می‌رود " +
                            "و بسیاری از اشعارش در میان مردم ایران و جهان فارسی‌زبان " +
                            "شناخته‌شده و مورد استفاده قرار می‌گیرد.",
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
            onSearchClick = {}
        )
    }
}
