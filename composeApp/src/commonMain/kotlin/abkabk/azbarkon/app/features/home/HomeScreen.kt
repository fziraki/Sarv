package abkabk.azbarkon.app.features.home

import abkabk.azbarkon.app.core.presentation.BaseScreen
import abkabk.azbarkon.app.core.util.Constants.BASE_URL
import abkabk.azbarkon.app.domain.model.Poet
import abkabk.azbarkon.app.ui.components.NetworkImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import azbarkoncmp.composeapp.generated.resources.Res
import azbarkoncmp.composeapp.generated.resources.favorite
import azbarkoncmp.composeapp.generated.resources.likes
import azbarkoncmp.composeapp.generated.resources.palette
import azbarkoncmp.composeapp.generated.resources.pic_negar
import azbarkoncmp.composeapp.generated.resources.review
import azbarkoncmp.composeapp.generated.resources.search
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

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

    Napier.d("HomeContent loaded")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TopSlider()
        }
        item {
            HeroCard()
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
                text = "شاعران محبوب",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "همه",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
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
            modifier = Modifier.fillMaxWidth(),
            imageUrl = item.imageUrl?.let {
                BASE_URL.plus(it.removePrefix("/"))
            }?:run{""}
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = item.name?:"",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
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
            ).padding(vertical = 24.dp, horizontal = 4.dp),
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
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun HeroCard() {

}

@Composable
fun TopSlider() {

}