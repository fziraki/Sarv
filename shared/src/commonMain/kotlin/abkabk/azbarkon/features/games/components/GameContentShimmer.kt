package abkabk.azbarkon.features.games.components

import abkabk.azbarkon.domain.model.games.GameType
import abkabk.azbarkon.ui.components.ShimmerPlaceholder
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GameContentShimmer(
    gameType: GameType,
    modifier: Modifier = Modifier,
) {
    when (gameType) {
        GameType.NEXT_VERSE -> NextVerseContentShimmer(modifier)
        GameType.FIND_POET -> FindPoetContentShimmer(modifier)
        GameType.COMPLETE_POEM -> CompletePoemContentShimmer(modifier)
        GameType.ORGANIZE_POEM -> OrganizePoemContentShimmer(modifier)
    }
}

@Composable
private fun NextVerseContentShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GamePoemCard {
            ShimmerBar(height = 14.dp, widthFraction = 0.35f)
            ShimmerBar(height = 20.dp)
            ShimmerBar(height = 20.dp)
        }

        ShimmerInstructionBar()

        repeat(4) {
            ShimmerBar(
                height = 48.dp,
                shape = RoundedCornerShape(12.dp),
            )
        }
    }
}

@Composable
private fun FindPoetContentShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GamePoemCard {
            ShimmerBar(height = 14.dp, widthFraction = 0.35f)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ShimmerBar(height = 20.dp)
                ShimmerBar(height = 20.dp)
            }
        }

        ShimmerInstructionBar()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    repeat(2) {
                        Row(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            ShimmerCircle(size = 36.dp)
                            ShimmerBar(
                                modifier = Modifier.weight(1f),
                                height = 16.dp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompletePoemContentShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GamePoemCard {
            ShimmerBar(height = 14.dp, widthFraction = 0.35f)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ShimmerBar(height = 20.dp)
                ShimmerBar(height = 20.dp)
            }
        }

        ShimmerInstructionBar()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    repeat(2) {
                        ShimmerBar(
                            modifier = Modifier.weight(1f),
                            height = 48.dp,
                            shape = RoundedCornerShape(12.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrganizePoemContentShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GamePoemCard {
            ShimmerBar(height = 14.dp, widthFraction = 0.35f)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) {
                    ShimmerBar(height = 20.dp)
                }
            }
        }

        ShimmerInstructionBar()

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(4) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ShimmerBar(
                        height = 24.dp,
                        widthFraction = 0.08f,
                        shape = RoundedCornerShape(4.dp),
                    )
                    ShimmerBar(
                        modifier = Modifier.weight(1f),
                        height = 16.dp,
                    )
                }
            }
        }
    }
}

@Composable
private fun ShimmerInstructionBar(
    height: Dp = 16.dp,
    widthFraction: Float = 0.6f,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        ShimmerBar(height = height, widthFraction = widthFraction)
    }
}

@Composable
private fun ShimmerBar(
    modifier: Modifier = Modifier,
    height: Dp,
    widthFraction: Float? = null,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    Box(
        modifier =
            modifier
                .then(
                    if (widthFraction != null) {
                        Modifier.fillMaxWidth(widthFraction)
                    } else {
                        Modifier.fillMaxWidth()
                    },
                ).height(height)
                .clip(shape),
    ) {
        ShimmerPlaceholder(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun ShimmerCircle(size: Dp) {
    Box(
        modifier =
            Modifier
                .size(size)
                .clip(CircleShape),
    ) {
        ShimmerPlaceholder(modifier = Modifier.fillMaxSize())
    }
}

@Preview
@Composable
private fun NextVerseContentShimmerPreview() {
    AzbarkonTheme {
        GameContentShimmer(
            gameType = GameType.NEXT_VERSE,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun FindPoetContentShimmerPreview() {
    AzbarkonTheme {
        GameContentShimmer(
            gameType = GameType.FIND_POET,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun CompletePoemContentShimmerPreview() {
    AzbarkonTheme {
        GameContentShimmer(
            gameType = GameType.COMPLETE_POEM,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview
@Composable
private fun OrganizePoemContentShimmerPreview() {
    AzbarkonTheme {
        GameContentShimmer(
            gameType = GameType.ORGANIZE_POEM,
            modifier = Modifier.padding(16.dp),
        )
    }
}
