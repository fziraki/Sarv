package abkabk.azbarkon.app.features.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import azbarkoncmp.composeapp.generated.resources.Res
import azbarkoncmp.composeapp.generated.resources.games
import azbarkoncmp.composeapp.generated.resources.missed_word_desc
import azbarkoncmp.composeapp.generated.resources.missed_word_score
import azbarkoncmp.composeapp.generated.resources.missed_word_title
import azbarkoncmp.composeapp.generated.resources.next_line_desc
import azbarkoncmp.composeapp.generated.resources.next_line_score
import azbarkoncmp.composeapp.generated.resources.next_line_title
import azbarkoncmp.composeapp.generated.resources.poetry_arrangement_desc
import azbarkoncmp.composeapp.generated.resources.poetry_arrangement_score
import azbarkoncmp.composeapp.generated.resources.poetry_arrangement_title
import azbarkoncmp.composeapp.generated.resources.whois_poet_desc
import azbarkoncmp.composeapp.generated.resources.whois_poet_score
import azbarkoncmp.composeapp.generated.resources.whois_poet_title
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun GamesScreen() {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 24.dp, horizontal = 16.dp)
    ) {
        item {
            GameItem(
                title = Res.string.next_line_title,
                desc = Res.string.next_line_desc,
                score = Res.string.next_line_score,
                icon = Res.drawable.games,
                onClick = {}
            )
        }

        item {
            GameItem(
                title = Res.string.missed_word_title,
                desc = Res.string.missed_word_desc,
                score = Res.string.missed_word_score,
                icon = Res.drawable.games,
                onClick = {}
            )
        }

        item {
            GameItem(
                title = Res.string.poetry_arrangement_title,
                desc = Res.string.poetry_arrangement_desc,
                score = Res.string.poetry_arrangement_score,
                icon = Res.drawable.games,
                onClick = {}
            )
        }

        item {
            GameItem(
                title = Res.string.whois_poet_title,
                desc = Res.string.whois_poet_desc,
                score = Res.string.whois_poet_score,
                icon = Res.drawable.games,
                onClick = {}
            )
        }
    }
}

@Composable
fun GameItem(
    title: StringResource,
    desc: StringResource,
    score: StringResource,
    icon: DrawableResource,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.clickable{
            onClick()
        }.fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
    ) {

        Image(
            modifier = Modifier.weight(0.3f)
                .fillMaxHeight()
                .background(
                color = MaterialTheme.colorScheme.secondary
            ),
            painter = painterResource(icon),
            contentDescription = null
        )

        Column(
            modifier = Modifier.weight(0.7f)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer
                )
            .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.End
        ) {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(desc),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Start
            )

            Text(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                text = "${stringResource(score)} امتیاز ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.End
            )
        }

    }
}