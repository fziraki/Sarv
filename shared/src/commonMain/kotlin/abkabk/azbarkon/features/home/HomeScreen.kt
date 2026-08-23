package abkabk.azbarkon.features.home

import abkabk.azbarkon.core.designsystem.brown
import abkabk.azbarkon.core.notifications.MAX_NOTIFICATION_PERMISSION_DECLINES
import abkabk.azbarkon.core.notifications.NotificationPermissionSheet
import abkabk.azbarkon.core.uidata.BaseScreen
import abkabk.azbarkon.core.uidata.LocalAzbarkonAppState
import abkabk.azbarkon.core.uidata.ObserveAsEvents
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.RandomDistich
import abkabk.azbarkon.domain.platform.NotificationPermissionGateway
import abkabk.azbarkon.domain.repository.UserPreferencesRepository
import abkabk.azbarkon.ui.components.AzbarkonButton
import abkabk.azbarkon.ui.components.AzbarkonPrimaryButton
import abkabk.azbarkon.ui.components.NetworkImage
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import abkabk.azbarkon.ui.theme.DarkColorScheme
import abkabk.azbarkon.ui.theme.LightColorScheme
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.all
import azbarkoncmp.shared.generated.resources.continue_memorization_desc
import azbarkoncmp.shared.generated.resources.continue_memorization_title
import azbarkoncmp.shared.generated.resources.herobg
import azbarkoncmp.shared.generated.resources.image_creator_bg
import azbarkoncmp.shared.generated.resources.memorization_button
import azbarkoncmp.shared.generated.resources.my_poems
import azbarkoncmp.shared.generated.resources.new_memorization_button
import azbarkoncmp.shared.generated.resources.new_memorization_desc
import azbarkoncmp.shared.generated.resources.new_memorization_title
import azbarkoncmp.shared.generated.resources.newsstand
import azbarkoncmp.shared.generated.resources.next_verse_game_bg
import azbarkoncmp.shared.generated.resources.palette
import azbarkoncmp.shared.generated.resources.pic_negar
import azbarkoncmp.shared.generated.resources.poetry_memorization
import azbarkoncmp.shared.generated.resources.popular_poets
import azbarkoncmp.shared.generated.resources.review
import azbarkoncmp.shared.generated.resources.search
import azbarkoncmp.shared.generated.resources.slider_beyt_of_day_poet
import azbarkoncmp.shared.generated.resources.slider_beyt_of_day_text
import azbarkoncmp.shared.generated.resources.slider_beyt_of_day_title
import azbarkoncmp.shared.generated.resources.slider_challenge_button
import azbarkoncmp.shared.generated.resources.slider_challenge_text
import azbarkoncmp.shared.generated.resources.slider_challenge_title
import azbarkoncmp.shared.generated.resources.slider_tasvir_negar_button
import azbarkoncmp.shared.generated.resources.slider_tasvir_negar_text
import azbarkoncmp.shared.generated.resources.slider_tasvir_negar_title
import azbarkoncmp.shared.generated.resources.today_distich_bg
import azbarkoncmp.shared.generated.resources.unknown
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

private const val SLIDER_TOP_WEIGHT = 0.4f
private const val SLIDER_CONTENT_WEIGHT = 0.6f
private const val SLIDER_BUTTON_WIDTH_FRACTION = 0.6f

data class HomeCallbacks(
    val onNavigateToPoetsList: () -> Unit,
    val onNavigateToPoetDetail: (Int) -> Unit,
    val onNavigateToPoemDetail: (Int) -> Unit,
    val onNavigateToMyPoems: () -> Unit,
    val onNavigateToSearch: () -> Unit,
    val onNavigateToTasvirNegar: () -> Unit,
    val onNavigateToMemorizationSelect: () -> Unit,
    val onNavigateToMemorizationPractice: () -> Unit,
    val onNavigateToActiveMemorization: () -> Unit,
    val onNavigateToGame: () -> Unit = {},
)

@Composable
fun HomeRoot(
    callbacks: HomeCallbacks,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appState = LocalAzbarkonAppState.current
    val permissionGateway: NotificationPermissionGateway = koinInject()
    val userPreferencesRepository: UserPreferencesRepository = koinInject()
    var showNotificationPermissionSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!appState.notificationPermissionSheetShownThisLaunch &&
            !permissionGateway.areNotificationsEnabled() &&
            userPreferencesRepository.getNotificationPermissionDeclineCount() < MAX_NOTIFICATION_PERMISSION_DECLINES
        ) {
            appState.notificationPermissionSheetShownThisLaunch = true
            showNotificationPermissionSheet = true
        }
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            HomeEvent.NavigateToPoetsList -> callbacks.onNavigateToPoetsList()

            is HomeEvent.NavigateToPoetDetail -> callbacks.onNavigateToPoetDetail(event.poetId)

            is HomeEvent.NavigateToPoemDetail -> callbacks.onNavigateToPoemDetail(event.poemId)

            HomeEvent.NavigateToMyPoems -> callbacks.onNavigateToMyPoems()

            HomeEvent.NavigateToSearch -> callbacks.onNavigateToSearch()

            HomeEvent.NavigateToTasvirNegar -> callbacks.onNavigateToTasvirNegar()

            HomeEvent.NavigateToMemorizationSelect -> callbacks.onNavigateToMemorizationSelect()

            HomeEvent.NavigateToMemorizationPractice -> callbacks.onNavigateToMemorizationPractice()

            HomeEvent.NavigateToActiveMemorization -> callbacks.onNavigateToActiveMemorization()

            HomeEvent.NavigateToGame -> callbacks.onNavigateToGame()
        }
    }

    BaseScreen(
        screenState = state.screenState,
    ) {
        HomeScreen(
            state = state,
            onAction = viewModel::onAction,
        )
    }

    if (showNotificationPermissionSheet) {
        NotificationPermissionSheet(
            onDismiss = { showNotificationPermissionSheet = false },
            onResult = { showNotificationPermissionSheet = false },
        )
    }
}

@Composable
fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        item {
    TopSlider(
        items =
            listOf(
                SliderPage.BeytOfDay,
                SliderPage.Challenge,
                SliderPage.TasvirNegar,
            ),
        todayDistich = state.todayDistich,
        onTasvirNegarClick = { onAction(HomeAction.OnTasvirNegarClick) },
        onChallengeClick = { onAction(HomeAction.OnChallengeClick) },
        onBeytOfDayClick = { onAction(HomeAction.OnBeytOfDayClick) },
    )
        }
        item {
            HeroCard(
                hero = state.memorizationHero,
                onClick = { onAction(HomeAction.OnMemorizationClick) },
            )
        }
        item {
            QuickAccessMenu(
                onMyPoemsClick = { onAction(HomeAction.OnMyPoemsClick) },
                onSearchClick = { onAction(HomeAction.OnSearchClick) },
                onTasvirNegarClick = { onAction(HomeAction.OnTasvirNegarClick) },
                onReviewClick = { onAction(HomeAction.OnReviewClick) },
            )
        }
        item {
            Poets(
                poets = state.poets,
                onSeeAllClick = { onAction(HomeAction.OnSeeAllPoetsClick) },
                onPoetClick = { poetId -> onAction(HomeAction.OnPoetClick(poetId)) },
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    AzbarkonTheme {
        HomeScreen(
            state = HomeState(),
            onAction = {},
        )
    }
}

@Composable
fun Poets(
    poets: List<Poet>,
    onSeeAllClick: () -> Unit,
    onPoetClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.popular_poets),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                modifier = Modifier.clickable(onClick = onSeeAllClick),
                text = stringResource(Res.string.all),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(
                items = poets,
                key = { poet -> poet.id ?: poet.name.orEmpty() },
            ) { poet ->
                PoetItem(
                    item = poet,
                    onClick = {
                        poet.id?.let(onPoetClick)
                    },
                )
            }
        }
    }
}

@Composable
fun PoetItem(
    item: Poet,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .width(80.dp)
                .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (item.imageUrl != null) {
            NetworkImage(
                modifier =
                    Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                imageUrl = item.imageUrl,
            )
        } else {
            Image(
                modifier =
                    Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(color = MaterialTheme.colorScheme.primary),
                painter = painterResource(Res.drawable.unknown),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.surface)
            )
        }
        Text(
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            text = item.name ?: "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun QuickAccessMenu(
    onMyPoemsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onTasvirNegarClick: () -> Unit,
    onReviewClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        QuickAccessItem(
            modifier = Modifier.weight(1f),
            icon = Res.drawable.newsstand,
            title = Res.string.my_poems,
            onItemClick = onMyPoemsClick,
        )

        QuickAccessItem(
            modifier = Modifier.weight(1f),
            icon = Res.drawable.search,
            title = Res.string.search,
            onItemClick = onSearchClick,
        )

        QuickAccessItem(
            modifier = Modifier.weight(1f),
            icon = Res.drawable.palette,
            title = Res.string.pic_negar,
            onItemClick = onTasvirNegarClick,
        )

        QuickAccessItem(
            modifier = Modifier.weight(1f),
            icon = Res.drawable.review,
            title = Res.string.review,
            onItemClick = onReviewClick,
        )
    }
}

@Composable
fun QuickAccessItem(
    icon: DrawableResource,
    title: StringResource,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Surface(
        modifier = modifier.clickable { onItemClick() },
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {

        Column(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }

}

@Composable
fun HeroCard(
    hero: MemorizationHeroUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val titleText =
        if (hero.hasActivePoems) {
            stringResource(Res.string.continue_memorization_title)
        } else {
            stringResource(Res.string.new_memorization_title)
        }
    val descText =
        if (hero.hasActivePoems) {
            stringResource(
                Res.string.continue_memorization_desc,
                hero.activePoemCount,
                hero.dueCardsToday,
            )
        } else {
            stringResource(Res.string.new_memorization_desc)
        }
    val buttonText =
        if (hero.hasActivePoems) {
            stringResource(Res.string.memorization_button)
        } else {
            stringResource(Res.string.new_memorization_button)
        }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
    ) {
        Image(
            painter = painterResource(Res.drawable.herobg),
            contentDescription = null,
            modifier = Modifier.matchParentSize()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Row(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = stringResource(Res.string.poetry_memorization),
                    style = MaterialTheme.typography.bodySmall,
                    color = LightColorScheme.onSecondary,
                )
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.headlineLarge,
                    color = LightColorScheme.surfaceVariant,
                )
                Text(
                    text = descText,
                    style = MaterialTheme.typography.labelSmall,
                    color = LightColorScheme.onSecondary,
                )
                AzbarkonButton(
                    text = buttonText,
                    onClick = onClick,
                    colors =
                        ButtonColors(
                            containerColor = brown,
                            contentColor = LightColorScheme.onPrimary,
                            disabledContainerColor = brown,
                            disabledContentColor = LightColorScheme.onPrimary,
                        ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopSlider(
    items: List<SliderPage>,
    modifier: Modifier = Modifier,
    autoPlayDuration: Long = 4000L,
    todayDistich: RandomDistich? = null,
    onTasvirNegarClick: () -> Unit = {},
    onChallengeClick: () -> Unit = {},
    onBeytOfDayClick: () -> Unit = {},
) {
    if (items.isEmpty()) return

    val pagerState =
        rememberPagerState(
            initialPage = Int.MAX_VALUE / 2,
            pageCount = { Int.MAX_VALUE },
        )

    fun getItem(page: Int): SliderPage = items[page % items.size]

    // 🎬 AUTOPLAY (simple)
    LaunchedEffect(items.size) {
        while (true) {
            delay(autoPlayDuration)

            pagerState.animateScrollToPage(
                pagerState.currentPage + 1,
                animationSpec =
                    spring(
                        dampingRatio = 0.9f,
                        stiffness = 300f,
                    ),
            )
        }
    }

    Column(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(horizontal = 32.dp),
            beyondViewportPageCount = 1,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(180.dp),
        ) { page ->

            val item = getItem(page)

            when (item) {
                is SliderPage.BeytOfDay -> BeytOfDaySlide(distich = todayDistich, onClick = onBeytOfDayClick)

                is SliderPage.Challenge -> ChallengeSlide(onClick = onChallengeClick)

                is SliderPage.TasvirNegar -> TasvirNegarSlide(onClick = onTasvirNegarClick)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 🎯 Indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            val currentIndex = pagerState.currentPage % items.size

            repeat(items.size) { index ->
                Box(
                    modifier =
                        Modifier
                            .padding(horizontal = 4.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                if (currentIndex == index) {
                                    MaterialTheme.colorScheme.secondary
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                            ),
                )
            }
        }
    }
}

@Composable
fun TasvirNegarSlide(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {

    Box(modifier = modifier.fillMaxSize()) {

        Image(
            painter = painterResource(Res.drawable.image_creator_bg),
            contentDescription = null,
            modifier = Modifier.matchParentSize()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
        ) {

            Spacer(modifier = Modifier.weight(SLIDER_TOP_WEIGHT))


            Column(
                modifier = Modifier.weight(SLIDER_CONTENT_WEIGHT).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.slider_tasvir_negar_title),
                    color = LightColorScheme.secondary,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.End,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.slider_tasvir_negar_text),
                    color = LightColorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.End,
                )

                AzbarkonButton(
                    text = stringResource(Res.string.slider_tasvir_negar_button),
                    onClick = onClick,
                    modifier =
                        Modifier
                            .fillMaxWidth(SLIDER_BUTTON_WIDTH_FRACTION)
                            .height(36.dp),
                    textStyle = MaterialTheme.typography.labelSmall,
                    colors =
                        ButtonColors(
                            containerColor = LightColorScheme.secondary,
                            contentColor = LightColorScheme.onSecondary,
                            disabledContainerColor = LightColorScheme.secondary,
                            disabledContentColor = LightColorScheme.onSecondary,
                        ),
                )
            }

        }
    }
}

@Composable
fun ChallengeSlide(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Box(modifier = modifier.fillMaxSize()) {

        Image(
            painter = painterResource(Res.drawable.next_verse_game_bg),
            contentDescription = null,
            modifier = Modifier.matchParentSize()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Row(
            modifier =
                Modifier
                    .fillMaxSize().padding(16.dp),
        ) {

            Spacer(modifier = Modifier.weight(SLIDER_TOP_WEIGHT))

            Column(
                modifier = Modifier.weight(SLIDER_CONTENT_WEIGHT).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.slider_challenge_title),
                    color = LightColorScheme.tertiary,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.End,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.slider_challenge_text),
                    color = LightColorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.End,
                )

                AzbarkonButton(
                    text = stringResource(Res.string.slider_challenge_button),
                    onClick = onClick,
                    modifier =
                        Modifier
                            .fillMaxWidth(SLIDER_BUTTON_WIDTH_FRACTION)
                            .height(36.dp),
                    textStyle = MaterialTheme.typography.labelSmall,
                    colors =
                        ButtonColors(
                            containerColor = LightColorScheme.onSurfaceVariant,
                            contentColor = LightColorScheme.surfaceVariant,
                            disabledContainerColor = LightColorScheme.onSurfaceVariant,
                            disabledContentColor = LightColorScheme.surfaceVariant,
                        ),
                )
            }

        }
    }
}

@Composable
fun BeytOfDaySlide(
    modifier: Modifier = Modifier,
    distich: RandomDistich? = null,
    onClick: () -> Unit = {},
) {
    val beytText = buildString {
        distich?.rightText?.let { append(it) }
        if (distich != null) append("\n")
        distich?.leftText?.let { append(it) }
    }.ifEmpty { stringResource(Res.string.slider_beyt_of_day_text) }
    val poetText = distich?.poetName ?: stringResource(Res.string.slider_beyt_of_day_poet)

    Box(modifier = modifier
        .shadow(
            shape = RoundedCornerShape(16.dp),
            elevation = 1.dp,
            spotColor = MaterialTheme.colorScheme.tertiary,
            ambientColor = MaterialTheme.colorScheme.tertiary
        )
        .clickable(enabled = distich != null, onClick = onClick)
        .fillMaxSize()
    ){

        Image(
            painter = painterResource(Res.drawable.today_distich_bg),
            contentDescription = null,
            modifier = Modifier.matchParentSize()
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )

        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            Column(
                modifier = Modifier.weight(SLIDER_CONTENT_WEIGHT).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.slider_beyt_of_day_title),
                    color = LightColorScheme.tertiary,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Start,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = beytText,
                    color = LightColorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = poetText,
                    color = LightColorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.End,
                )
            }

            Spacer(modifier = Modifier.weight(SLIDER_TOP_WEIGHT))

        }
    }

}
