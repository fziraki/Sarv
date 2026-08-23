package abkabk.azbarkon.features.poets.list

import abkabk.azbarkon.core.uidata.BaseScreen
import abkabk.azbarkon.core.uidata.ObserveAsEvents
import abkabk.azbarkon.features.poets.FeaturedPoetUi
import abkabk.azbarkon.features.poets.PoetListItemUi
import abkabk.azbarkon.ui.components.AzbarkonButton
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import abkabk.azbarkon.ui.theme.LightColorScheme
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.cd_chat
import azbarkoncmp.shared.generated.resources.chat_bubble
import azbarkoncmp.shared.generated.resources.download
import azbarkoncmp.shared.generated.resources.poets_download
import azbarkoncmp.shared.generated.resources.poets_filter_placeholder
import azbarkoncmp.shared.generated.resources.poets_view_works
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PoetsListRoot(
    onNavigateToPoetDetail: (Int) -> Unit,
    onNavigateToChat: (Int) -> Unit,
    viewModel: PoetsListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is PoetsListEvent.NavigateToPoetDetail -> onNavigateToPoetDetail(event.poetId)
            is PoetsListEvent.NavigateToChat -> onNavigateToChat(event.poetId)
        }
    }

    LifecycleResumeEffect(Unit) {
        viewModel.onAction(PoetsListAction.OnScreenEnter)
        onPauseOrDispose { }
    }

    BaseScreen(
        screenState = state.screenState,
    ) {
        PoetsListScreen(
            state = state,
            onAction = viewModel::onAction,
        )
    }
}

@Composable
fun PoetsListScreen(
    state: PoetsListState,
    onAction: (PoetsListAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize().padding(top = 16.dp)
                .background(MaterialTheme.colorScheme.background),
    ) {

        FilterField(
            value = state.searchQuery,
            placeholder = stringResource(Res.string.poets_filter_placeholder),
            onValueChange = { onAction(PoetsListAction.OnSearchQueryChange(it)) },
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            state.featuredPoet?.let { featured ->
                item {
                    FeaturedPoetCard(
                        poet = featured,
                        onClick = { onAction(PoetsListAction.OnFeaturedPoetClick) },
                        onChatClick = { onAction(PoetsListAction.OnChatClick(featured.id)) },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }

            items(
                items = state.poets,
                key = { poet -> poet.id },
            ) { poet ->
                PoetListRow(
                    poet = poet,
                    isDownloading = poet.id in state.downloadingPoetIds,
                    onClick = {
                        if (poet.isDownloaded) {
                            onAction(PoetsListAction.OnPoetClick(poet.id))
                        } else {
                            onAction(PoetsListAction.OnDownloadPoet(poet.id))
                        }
                    },
                    onDownloadClick = { onAction(PoetsListAction.OnDownloadPoet(poet.id)) },
                    onChatClick = { onAction(PoetsListAction.OnChatClick(poet.id)) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun FeaturedPoetCard(
    poet: FeaturedPoetUi,
    onClick: () -> Unit,
    onChatClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(LightColorScheme.primary)
                .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onClick),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PoetAvatar(
                imageUrl = poet.imageUrl,
                modifier = Modifier.size(72.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = poet.name,
                    style = MaterialTheme.typography.headlineLarge,
                    color = LightColorScheme.onPrimary,
                )
                Text(
                    text = poet.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightColorScheme.onPrimary.copy(alpha = 0.9f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (poet.stats.isNotBlank()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = poet.stats,
                        style = MaterialTheme.typography.labelMedium,
                        color = LightColorScheme.surfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            if (poet.canChat) {
                Icon(
                    painter = painterResource(Res.drawable.chat_bubble),
                    contentDescription = stringResource(Res.string.cd_chat),
                    tint = LightColorScheme.surfaceVariant,
                    modifier =
                        Modifier
                            .clip(CircleShape)
                            .clickable(onClick = onChatClick)
                            .padding(4.dp),
                )
            }

            AzbarkonButton(
                text = stringResource(Res.string.poets_view_works),
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightColorScheme.secondary,
                    contentColor = LightColorScheme.onSecondary,
                    disabledContainerColor = LightColorScheme.secondary,
                    disabledContentColor = LightColorScheme.onSecondary,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
        }

    }
}

@Composable
private fun PoetListRow(
    poet: PoetListItemUi,
    isDownloading: Boolean,
    onClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onChatClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation =  CardDefaults.cardElevation(defaultElevation = 1.dp)
    ){
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {

            PoetAvatar(
                imageUrl = poet.imageUrl,
                modifier = Modifier.size(52.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = poet.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start,
                )
                if (poet.worksSummary.isNotBlank()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = poet.worksSummary,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            if (!poet.isDownloaded) {
                if (isDownloading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Icon(
                        painter = painterResource(Res.drawable.download),
                        contentDescription = stringResource(Res.string.poets_download),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier =
                            Modifier
                                .clip(CircleShape)
                                .clickable(onClick = onDownloadClick)
                                .padding(4.dp),
                    )
                }
            } else if (poet.canChat) {
                Icon(
                    painter = painterResource(Res.drawable.chat_bubble),
                    contentDescription = stringResource(Res.string.cd_chat),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier =
                        Modifier
                            .clip(CircleShape)
                            .clickable(onClick = onChatClick)
                            .padding(4.dp),
                )
            }
        }
    }

}

@Preview
@Composable
private fun PoetsListScreenPreview() {
    AzbarkonTheme {
        PoetsListScreen(
            state =
                PoetsListState(
                    poets =
                        listOf(
                            PoetListItemUi(
                                id = 7,
                                name = "سعدی شیرازی",
                                worksSummary = "گلستان و 1 اثر دیگر",
                                imageUrl = null,
                                canChat = false,
                                isDownloaded = false,
                            ),
                        ),
                    featuredPoet =
                        FeaturedPoetUi(
                            id = 2,
                            name = "حافظ شیرازی",
                            description = "غزل‌سرای بزرگ ایران",
                            stats = "قطعات و 4 اثر دیگر",
                            imageUrl = null,
                            canChat = true,
                        ),
                ),
            onAction = {},
        )
    }
}
