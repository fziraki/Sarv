package abkabk.azbarkon.app.features.home

import abkabk.azbarkon.app.core.presentation.BaseScreen
import abkabk.azbarkon.app.core.presentation.UiText
import abkabk.azbarkon.app.core.presentation.asString
import abkabk.azbarkon.app.core.util.Constants.BASE_URL
import abkabk.azbarkon.app.domain.model.Poet
import abkabk.azbarkon.app.ui.components.NetworkImage
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import azbarkoncmp.composeapp.generated.resources.Res
import azbarkoncmp.composeapp.generated.resources.all
import azbarkoncmp.composeapp.generated.resources.favorite
import azbarkoncmp.composeapp.generated.resources.likes
import azbarkoncmp.composeapp.generated.resources.memorization_button
import azbarkoncmp.composeapp.generated.resources.new_memorization_button
import azbarkoncmp.composeapp.generated.resources.new_memorization_desc
import azbarkoncmp.composeapp.generated.resources.new_memorization_title
import azbarkoncmp.composeapp.generated.resources.palette
import azbarkoncmp.composeapp.generated.resources.pic_negar
import azbarkoncmp.composeapp.generated.resources.poetry_memorization
import azbarkoncmp.composeapp.generated.resources.popular_poets
import azbarkoncmp.composeapp.generated.resources.review
import azbarkoncmp.composeapp.generated.resources.search
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.abs

@Composable
fun HomeScreen(){

    val viewModel: HomeViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    BaseScreen(
        screenState = state.screenState,
        effectFlow = viewModel.effect,
        onRetry = {
            viewModel.onEvent(HomeContract.Event.Retry)
        },
        onEffect = {
            when(it){
                is HomeContract.Effect.ShowSnackbar -> {

                }
            }
        }
    ) {

        HomeContent(state)
    }

}

@Composable
fun HomeContent(state: HomeContract.State) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            TopSlider(
                items = listOf(
                    SliderPage.BeytOfDay,
                    SliderPage.Challenge,
                    SliderPage.TasvirNegar
                )
            )
        }
        item {
            HeroCard(state.isNewMemorization)
        }
        item {
            QuickAccessMenu()
        }
        item {
            Poets(state.poets)
        }
    }
}

@Composable
fun Poets(poets: List<Poet>) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(Res.string.popular_poets),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(Res.string.all),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            poets.take(4).forEach {
                PoetItem(it)
            }
        }

    }
}

@Composable
fun PoetItem(item: Poet) {

    Column(
        modifier = Modifier.width(80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        NetworkImage(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            imageUrl = item.imageUrl?.let {
                BASE_URL.plus(it.removePrefix("/"))
            }?:run{""}
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            text = item.name?:"",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun QuickAccessMenu() {

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
       QuickAccessItem(
           modifier = Modifier.weight(1f),
           icon = Res.drawable.favorite,
           title = Res.string.likes,
           onItemClick = {

           }
       )

        QuickAccessItem(
            modifier = Modifier.weight(1f),
            icon = Res.drawable.search,
            title = Res.string.search,
            onItemClick = {

            },
        )

        QuickAccessItem(
            modifier = Modifier.weight(1f),
            icon = Res.drawable.palette,
            title = Res.string.pic_negar,
            onItemClick = {

            },
        )

        QuickAccessItem(
            modifier = Modifier.weight(1f),
            icon = Res.drawable.review,
            title = Res.string.review,
            onItemClick = {

            },
        )
    }
}

@Composable
fun QuickAccessItem(
    modifier: Modifier,
    icon: DrawableResource,
    title: StringResource,
    onItemClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clickable {
                onItemClick()
            }
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(24.dp)
            ).padding(vertical = 20.dp, horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun HeroCard(newMemorization: Boolean) {

    val res1 = Res.string.poetry_memorization
    val res2 = if (newMemorization){
        stringResource(Res.string.new_memorization_title)
    } else {
        UiText.Dynamic("").asString()
    }
    val res3 = if (newMemorization){
        stringResource(Res.string.new_memorization_desc)
    } else {
        UiText.Dynamic("").asString()
    }
    val resButton = if (newMemorization) { Res.string.new_memorization_button } else {
        Res.string.memorization_button
    }

    Box(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp)
        .background(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(12.dp)
        )
    ){

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            Image(
                modifier = Modifier.weight(1f),
                painter = painterResource(Res.drawable.palette),
                contentDescription = null
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stringResource(res1),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = res2,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = res3,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary,
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    onClick = {}
                ){
                    Text(
                        text = stringResource(resButton),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopSlider(
    items: List<SliderPage>,
    modifier: Modifier = Modifier,
    autoPlayDuration: Long = 4000L
) {
    if (items.isEmpty()) return

    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )

    fun getItem(page: Int): SliderPage {
        return items[page % items.size]
    }

    // 🎬 AUTOPLAY (simple)
    LaunchedEffect(items.size) {
        while (true) {
            delay(autoPlayDuration)

            pagerState.animateScrollToPage(
                pagerState.currentPage + 1,
                animationSpec = spring(
                    dampingRatio = 0.9f,
                    stiffness = 300f
                )
            )
        }
    }

    Column(modifier = modifier) {

        HorizontalPager(
            state = pagerState,
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(horizontal = 32.dp),
            beyondViewportPageCount = 1,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) { page ->

            val item = getItem(page)

            when (item) {

                is SliderPage.BeytOfDay -> BeytOfDaySlide()

                is SliderPage.Challenge -> ChallengeSlide()

                is SliderPage.TasvirNegar -> TasvirNegarSlide()
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 🎯 Indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            val currentIndex = pagerState.currentPage % items.size

            repeat(items.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(
                            if (currentIndex == index)
                                MaterialTheme.colorScheme.secondary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }
    }
}

@Composable
fun TasvirNegarSlide() {
    Row(modifier = Modifier.fillMaxSize()
        .background(
            color = MaterialTheme.colorScheme.primaryFixedDim,
            shape = RoundedCornerShape(12.dp)
        )
        .padding(16.dp),
    ) {

        Image(
            modifier = Modifier.weight(0.4f),
            painter = painterResource(Res.drawable.palette),
            contentDescription = null
        )


        Column(
            modifier = Modifier.weight(0.6f).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "تصویرنگار",
                color = MaterialTheme.colorScheme.onSecondaryFixedVariant,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.End
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "شعر خود را\nتصویر کنید",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End
            )

            Button(
                modifier = Modifier.fillMaxWidth(0.6f)
                    .height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondaryFixedVariant,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSecondaryFixedVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSecondary,
                ),
                onClick = {}
            ){
                Text(
                    text = "شروع ساخت",
                    style = MaterialTheme.typography.labelSmall
                )
            }

        }

    }
}

@Composable
fun ChallengeSlide() {
    Row(modifier = Modifier.fillMaxSize()
        .background(
            color = MaterialTheme.colorScheme.tertiaryFixedDim,
            shape = RoundedCornerShape(12.dp)
        )
        .padding(16.dp),
    ) {

        Image(
            modifier = Modifier.weight(0.4f),
            painter = painterResource(Res.drawable.palette),
            contentDescription = null
        )


        Column(
            modifier = Modifier.weight(0.6f).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End
        ) {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "چالش روز",
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.End
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "مصرع بعدی این بیت\nرا بلدی؟",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End
            )

            Button(
                modifier = Modifier.fillMaxWidth(0.6f)
                    .height(36.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.onTertiaryFixedVariant,
                    contentColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContainerColor = MaterialTheme.colorScheme.onTertiaryFixedVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onTertiaryFixed,
                ),
                onClick = {}
            ){
                Text(
                    text = "شروع چالش",
                    style = MaterialTheme.typography.labelSmall
                )
            }

        }

    }
}

@Composable
fun BeytOfDaySlide() {
    Row(modifier = Modifier.fillMaxSize()
        .background(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp)
        )
        .padding(16.dp),
    ) {

        Column(
            modifier = Modifier.weight(0.6f).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "بیت امروز",
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Start
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "که عشق آسان نمود اول\nولی افتاد مشکل ها",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "حافظ",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.End
            )

        }

        Image(
            modifier = Modifier.weight(0.4f),
            painter = painterResource(Res.drawable.palette),
            contentDescription = null
        )
    }
}
