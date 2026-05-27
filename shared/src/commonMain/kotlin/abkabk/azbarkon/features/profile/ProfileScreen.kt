package abkabk.azbarkon.features.profile

import abkabk.azbarkon.core.ui_base.BaseScreen
import abkabk.azbarkon.domain.model.Badge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.palette
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen() {
    val viewModel: ProfileViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    BaseScreen(
        screenState = state.screenState,
        effectFlow = viewModel.effect,
        onRetry = {
            viewModel.onEvent(ProfileContract.Event.Retry)
        },
        onEffect = {
            when (it) {
                is ProfileContract.Effect.ShowSnackbar -> {
                }
            }
        },
    ) {
        ProfileContent(state)
    }
}

@Composable
fun ProfileContent(state: ProfileContract.State) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            ProfileHeader(
                completedLevelName = state.userInfo?.completedLevel?.name ?: "",
                inProgressLevelNumber = state.userInfo?.inProgressLevel?.id ?: 1,
                currentScore = state.userInfo?.currentScore ?: 0,
                totalScore = state.userInfo?.inProgressLevel?.totalScore ?: 0,
            )
        }

        item {
            ProfileStreak(
                streakNumber = state.userInfo?.streakNumber ?: 0,
                poetsNumber = state.userInfo?.poetsNumber ?: 0,
                poemsNumber = state.userInfo?.poemsNumber ?: 0,
            )
        }

        item {
            ProfileBadges(state.userInfo?.badges ?: emptyList())
        }
    }
}

@Composable
fun ProfileBadges(badges: List<Badge>) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp),
                ).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End,
    ) {
        Text(
            modifier = Modifier,
            text = "مشاهده همه",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.End,
        )

        LazyRow(modifier = Modifier.fillMaxWidth()) {
            badges.forEach {
                item {
                    BadgeItem(it)
                }
            }
        }
    }
}

@Composable
fun BadgeItem(item: Badge) {
    Column(
        modifier = Modifier.width(80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(painter = painterResource(Res.drawable.palette), contentDescription = null)
        Text(
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            text = item.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ProfileStreak(
    streakNumber: Int,
    poetsNumber: Int,
    poemsNumber: Int,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp),
                ).padding(24.dp),
    ) {
        StreakItem(
            modifier = Modifier.weight(1f),
            num = streakNumber,
            desc = "روز پیاپی",
        )

        VerticalDivider(
            modifier = Modifier.fillMaxHeight(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surface,
        )

        StreakItem(
            modifier = Modifier.weight(1f),
            num = poetsNumber,
            desc = "شاعر خوانده اید",
        )

        VerticalDivider(
            modifier = Modifier.fillMaxHeight(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.surface,
        )

        StreakItem(
            modifier = Modifier.weight(1f),
            num = poemsNumber,
            desc = "بیت حفظ شده",
        )
    }
}

@Composable
fun StreakItem(
    modifier: Modifier = Modifier,
    num: Int,
    desc: String,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier,
            text = num.toString(),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            modifier = Modifier,
            text = desc,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ProfileHeader(
    completedLevelName: String,
    inProgressLevelNumber: Int,
    currentScore: Int,
    totalScore: Int,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(Res.drawable.palette),
            contentDescription = null,
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = completedLevelName,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "سطح $inProgressLevelNumber",
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            LinearProgressIndicator(
                progress = { currentScore.toFloat() / totalScore.toFloat() },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(8.dp),
                color = ProgressIndicatorDefaults.linearColor,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = StrokeCap.Round,
                drawStopIndicator = {},
                gapSize = (-4).dp,
            )
        }
    }
}
