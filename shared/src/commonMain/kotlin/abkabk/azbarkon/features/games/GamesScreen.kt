package abkabk.azbarkon.features.games

import abkabk.azbarkon.domain.model.games.GameType
import abkabk.azbarkon.core.uidata.BaseScreen
import abkabk.azbarkon.core.uidata.UiScreenState
import abkabk.azbarkon.features.games.navigation.toRoute
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.complete_poem_desc
import azbarkoncmp.shared.generated.resources.complete_poem_title
import azbarkoncmp.shared.generated.resources.games
import azbarkoncmp.shared.generated.resources.next_line_desc
import azbarkoncmp.shared.generated.resources.next_line_title
import azbarkoncmp.shared.generated.resources.poetry_arrangement_desc
import azbarkoncmp.shared.generated.resources.poetry_arrangement_title
import azbarkoncmp.shared.generated.resources.whois_poet_desc
import azbarkoncmp.shared.generated.resources.whois_poet_title
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.ui.layout.ContentScale
import azbarkoncmp.shared.generated.resources.guess_poet_icon
import azbarkoncmp.shared.generated.resources.incomplete_icon
import azbarkoncmp.shared.generated.resources.next_verse_icon
import azbarkoncmp.shared.generated.resources.reorder_poem_icon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val GAME_CARD_IMAGE_WEIGHT = 0.3f
private const val GAME_CARD_CONTENT_WEIGHT = 0.7f

@Composable
fun GamesRoot(
    onNavigateToGame: (GameType) -> Unit,
) {
    BaseScreen(screenState = UiScreenState.Success) {
        GamesScreen(onNavigateToGame = onNavigateToGame)
    }
}

@Composable
fun GamesScreen(
    onNavigateToGame: (GameType) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 24.dp, horizontal = 16.dp),
    ) {
        item {
            GameItem(
                gameType = GameType.NEXT_VERSE,
                title = Res.string.next_line_title,
                desc = Res.string.next_line_desc,
                icon = Res.drawable.next_verse_icon,
                onClick = { onNavigateToGame(GameType.NEXT_VERSE) },
            )
        }

        item {
            GameItem(
                gameType = GameType.COMPLETE_POEM,
                title = Res.string.complete_poem_title,
                desc = Res.string.complete_poem_desc,
                icon = Res.drawable.incomplete_icon,
                onClick = { onNavigateToGame(GameType.COMPLETE_POEM) },
            )
        }


        item {
            GameItem(
                gameType = GameType.FIND_POET,
                title = Res.string.whois_poet_title,
                desc = Res.string.whois_poet_desc,
                icon = Res.drawable.guess_poet_icon,
                onClick = { onNavigateToGame(GameType.FIND_POET) },
            )
        }

        item {
            GameItem(
                gameType = GameType.ORGANIZE_POEM,
                title = Res.string.poetry_arrangement_title,
                desc = Res.string.poetry_arrangement_desc,
                icon = Res.drawable.reorder_poem_icon,
                onClick = { onNavigateToGame(GameType.ORGANIZE_POEM) },
            )
        }

    }
}

@Composable
fun GameItem(
    gameType: GameType,
    title: StringResource,
    desc: StringResource,
    icon: DrawableResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp,
        shadowElevation = 1.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row {
            Image(
                modifier =
                    Modifier
                        .weight(GAME_CARD_IMAGE_WEIGHT)
                        .fillMaxHeight()
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                        )
                        .clip(RoundedCornerShape(
                            topStart = 12.dp, bottomStart = 12.dp,
                            topEnd = 0.dp, bottomEnd = 0.dp
                        )),
                painter = painterResource(icon),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )

            Column(
                modifier =
                    Modifier
                        .weight(GAME_CARD_CONTENT_WEIGHT)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        ).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                )

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(desc),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                )

                Text(
                    modifier =
                        Modifier
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.tertiary,
                                shape = RoundedCornerShape(8.dp),
                            ).padding(horizontal = 8.dp, vertical = 4.dp),
                    text = "${gameType.baseScore} امتیاز ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.End,
                )
            }
        }
    }

}

@Preview
@Composable
private fun GamesScreenPreview() {
    AzbarkonTheme {
        GamesScreen(onNavigateToGame = {})
    }
}
