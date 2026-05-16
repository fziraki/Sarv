package abkabk.azbarkon.app.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import azbarkoncmp.composeapp.generated.resources.Res
import azbarkoncmp.composeapp.generated.resources.favorite
import azbarkoncmp.composeapp.generated.resources.likes
import azbarkoncmp.composeapp.generated.resources.palette
import azbarkoncmp.composeapp.generated.resources.pic_negar
import azbarkoncmp.composeapp.generated.resources.review
import azbarkoncmp.composeapp.generated.resources.search
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeScreen() {
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
            Poets()
        }
    }
}

@Composable
fun Poets() {
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