package abkabk.azbarkon.features.mypoems

import abkabk.azbarkon.core.uidata.BaseScreen
import abkabk.azbarkon.core.uidata.ObserveAsEvents
import abkabk.azbarkon.ui.components.SarvAlertDialog
import abkabk.azbarkon.ui.components.Header
import abkabk.azbarkon.ui.components.HeaderAction
import abkabk.azbarkon.ui.theme.SarvTheme
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sarv.shared.generated.resources.Res
import sarv.shared.generated.resources.bookmark_filled
import sarv.shared.generated.resources.cd_clear_bookmarked
import sarv.shared.generated.resources.cd_clear_liked
import sarv.shared.generated.resources.clear_confirm
import sarv.shared.generated.resources.clear_cancel
import sarv.shared.generated.resources.clear_dialog_bookmarked_body
import sarv.shared.generated.resources.clear_dialog_liked_body
import sarv.shared.generated.resources.clear_dialog_title
import sarv.shared.generated.resources.empty_bookmarked
import sarv.shared.generated.resources.empty_liked
import sarv.shared.generated.resources.tab_bookmarked
import sarv.shared.generated.resources.tab_liked
import sarv.shared.generated.resources.heart_filled
import sarv.shared.generated.resources.my_poems
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import abkabk.azbarkon.core.designsystem.SarvDimensions

@Composable
fun MyPoemsRoot(
    onBackClick: () -> Unit,
    onNavigateToPoemDetail: (Int) -> Unit,
    viewModel: MyPoemsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onAction(MyPoemsAction.OnResume)
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is MyPoemsEvent.NavigateToPoemDetail -> onNavigateToPoemDetail(event.poemId)
        }
    }

    BaseScreen(
        screenState = state.screenState,
    ) {
        MyPoemsScreen(
            state = state,
            onBackClick = onBackClick,
            onAction = viewModel::onAction,
        )
    }
}

@Composable
fun MyPoemsScreen(
    state: MyPoemsState,
    onBackClick: () -> Unit,
    onAction: (MyPoemsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val clearDialogBody =
        when (state.selectedTab) {
            MyPoemsTab.Liked -> stringResource(Res.string.clear_dialog_liked_body)
            MyPoemsTab.Bookmarked -> stringResource(Res.string.clear_dialog_bookmarked_body)
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        Header(
            title = stringResource(Res.string.my_poems),
            onBackClick = onBackClick,
            action =
                if (state.isActiveTabEmpty) {
                    null
                } else {
                    HeaderAction.ClearAll { onAction(MyPoemsAction.OnClearAllClick) }
                },
        )

        MyPoemsTabRow(
            selectedTab = state.selectedTab,
            onSelectTab = { tab -> onAction(MyPoemsAction.OnTabSelected(tab)) },
        )

        if (state.isActiveTabEmpty) {
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(SarvDimensions.dimen24),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text =
                        when (state.selectedTab) {
                            MyPoemsTab.Liked -> stringResource(Res.string.empty_liked)
                            MyPoemsTab.Bookmarked -> stringResource(Res.string.empty_bookmarked)
                        },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = SarvDimensions.dimen16, vertical = SarvDimensions.dimen24),
                verticalArrangement = Arrangement.spacedBy(SarvDimensions.dimen12),
            ) {
                state.activeGroups.forEach { poetGroup ->
                    poetGroup.categories.forEach { categoryGroup ->
                        items(
                            items = categoryGroup.poems,
                            key = { poem -> "${state.selectedTab}-${poem.id}" },
                        ) { poem ->
                            MyPoemRow(
                                poemTitle = poem.title,
                                poetName = poetGroup.poetName,
                                categoryName = categoryGroup.categoryName,
                                tab = state.selectedTab,
                                onPoemClick = { onAction(MyPoemsAction.OnPoemClick(poem.id)) },
                                onRemoveClick = { onAction(MyPoemsAction.OnRemovePoem(poem.id)) },
                            )
                        }
                    }
                }
            }
        }
    }

    if (state.showClearDialog) {
        SarvAlertDialog(
            onDismissRequest = { onAction(MyPoemsAction.OnClearAllDismiss) },
            title = stringResource(Res.string.clear_dialog_title),
            text = clearDialogBody,
            confirmLabel = stringResource(Res.string.clear_confirm),
            onConfirm = { onAction(MyPoemsAction.OnClearAllConfirm) },
            dismissLabel = stringResource(Res.string.clear_cancel),
        )
    }
}

@Composable
private fun MyPoemsTabRow(
    selectedTab: MyPoemsTab,
    onSelectTab: (MyPoemsTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val likedLabel = stringResource(Res.string.tab_liked)
    val bookmarkedLabel = stringResource(Res.string.tab_bookmarked)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(SarvDimensions.dimen48),
        ) {
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onSelectTab(MyPoemsTab.Liked) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = likedLabel,
                    style = MaterialTheme.typography.titleSmall,
                    color =
                        if (selectedTab == MyPoemsTab.Liked) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }

            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onSelectTab(MyPoemsTab.Bookmarked) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = bookmarkedLabel,
                    style = MaterialTheme.typography.titleSmall,
                    color =
                        if (selectedTab == MyPoemsTab.Bookmarked) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                )
            }
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(SarvDimensions.dimen3)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val tabWidth = maxWidth / 2
                val indicatorOffset by animateDpAsState(
                    targetValue = if (selectedTab == MyPoemsTab.Liked) SarvDimensions.dimen0 else tabWidth,
                    animationSpec = tween(durationMillis = 200),
                )

                Box(
                    modifier =
                        Modifier
                            .width(tabWidth)
                            .fillMaxHeight()
                            .offset(x = indicatorOffset)
                            .background(MaterialTheme.colorScheme.primary),
                )
            }
        }
    }
}

@Composable
private fun MyPoemRow(
    poemTitle: String,
    poetName: String,
    categoryName: String,
    tab: MyPoemsTab,
    onPoemClick: () -> Unit,
    onRemoveClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(SarvDimensions.dimen16))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = SarvDimensions.dimen1,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(SarvDimensions.dimen16),
                )
                .clickable(onClick = onPoemClick)
                .padding(start = SarvDimensions.dimen14, top = SarvDimensions.dimen14, bottom = SarvDimensions.dimen14, end = SarvDimensions.dimen8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SarvDimensions.dimen8),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(SarvDimensions.dimen4),
        ) {
            Text(
                text = poemTitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start,
            )
            Text(
                text = "$poetName · $categoryName",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start,
            )
        }

        Box(
            modifier =
                Modifier
                    .size(SarvDimensions.dimen40)
                    .clip(RoundedCornerShape(SarvDimensions.dimen12))
                    .clickable(onClick = onRemoveClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter =
                    painterResource(
                        when (tab) {
                            MyPoemsTab.Liked -> Res.drawable.heart_filled
                            MyPoemsTab.Bookmarked -> Res.drawable.bookmark_filled
                        },
                    ),
                contentDescription = stringResource(
                        when (tab) {
                            MyPoemsTab.Liked -> Res.string.cd_clear_liked
                            MyPoemsTab.Bookmarked -> Res.string.cd_clear_bookmarked
                        }
                    ),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(SarvDimensions.dimen22),
            )
        }
    }
}

@Preview
@Composable
private fun MyPoemsScreenPreview() {
    SarvTheme {
        MyPoemsScreen(
            state =
                MyPoemsState(
                    selectedTab = MyPoemsTab.Liked,
                    likedGroups =
                        listOf(
                            PoetGroupUi(
                                poetName = "حافظ",
                                categories =
                                    listOf(
                                        CategoryGroupUi(
                                            categoryName = "غزلیات",
                                            poems =
                                                listOf(
                                                    MyPoemItemUi(id = 1, title = "شمارهٔ ۱"),
                                                    MyPoemItemUi(id = 2, title = "شمارهٔ ۲"),
                                                ),
                                        ),
                                    ),
                            ),
                        ),
                ),
            onBackClick = {},
            onAction = {},
        )
    }
}
