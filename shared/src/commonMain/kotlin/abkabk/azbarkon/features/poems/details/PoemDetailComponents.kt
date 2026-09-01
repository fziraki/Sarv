package abkabk.azbarkon.features.poems.details

import abkabk.azbarkon.core.ui.HighlightedText
import abkabk.azbarkon.ui.theme.SarvTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sarv.shared.generated.resources.Res
import sarv.shared.generated.resources.add_box_24px
import sarv.shared.generated.resources.cd_add_poem
import sarv.shared.generated.resources.cd_context_search
import sarv.shared.generated.resources.cd_image_creator
import sarv.shared.generated.resources.cd_like
import sarv.shared.generated.resources.cd_share
import sarv.shared.generated.resources.heart
import sarv.shared.generated.resources.heart_filled
import sarv.shared.generated.resources.ornoment230l
import sarv.shared.generated.resources.ornoment230r
import sarv.shared.generated.resources.ornoment30
import sarv.shared.generated.resources.palette
import sarv.shared.generated.resources.poem_image_creator
import sarv.shared.generated.resources.poem_liked
import sarv.shared.generated.resources.poem_memorize
import sarv.shared.generated.resources.poem_share
import sarv.shared.generated.resources.search
import sarv.shared.generated.resources.share
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import abkabk.azbarkon.core.designsystem.SarvDimensions

@Composable
fun PoemVerseItem(
    verse: PoemVerseUi,
    modifier: Modifier = Modifier,
    highlightQuery: String = "",
) {
    when (verse.positionType) {
        PoemVersePositionType.Comment -> {
            CommentVerseItem(
                text = verse.text,
                highlightQuery = highlightQuery,
                modifier = modifier,
            )
        }

        PoemVersePositionType.Right -> {
            RightVerseItem(
                text = verse.text,
                highlightQuery = highlightQuery,
                modifier = modifier,
            )
        }

        PoemVersePositionType.Left -> {
            LeftVerseItem(
                text = verse.text,
                highlightQuery = highlightQuery,
                modifier = modifier,
            )
        }

        PoemVersePositionType.Center -> {
            CenterVerseItem(
                text = verse.text,
                highlightQuery = highlightQuery,
                modifier = modifier,
            )
        }

        PoemVersePositionType.Paragraph -> {
            ParagraphVerseItem(
                text = verse.text,
                highlightQuery = highlightQuery,
                modifier = modifier,
            )
        }

        PoemVersePositionType.Single -> {
            SingleVerseItem(
                text = verse.text,
                highlightQuery = highlightQuery,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun CommentVerseItem(
    text: String,
    highlightQuery: String,
    modifier: Modifier = Modifier,
) {
    HighlightedText(
        text = text,
        query = highlightQuery,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = SarvDimensions.dimen8)
                .clip(RoundedCornerShape(SarvDimensions.dimen8))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = SarvDimensions.dimen12, vertical = SarvDimensions.dimen10),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun RightVerseItem(
    text: String,
    highlightQuery: String,
    modifier: Modifier = Modifier,
) {
    HighlightedText(
        text = text,
        query = highlightQuery,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = SarvDimensions.dimen16),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Start,
    )
}

@Composable
private fun LeftVerseItem(
    text: String,
    highlightQuery: String,
    modifier: Modifier = Modifier,
) {
    HighlightedText(
        text = text,
        query = highlightQuery,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = SarvDimensions.dimen12),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.End,
    )
}

@Composable
private fun CenterVerseItem(
    text: String,
    highlightQuery: String,
    modifier: Modifier = Modifier,
) {
    HighlightedText(
        text = text,
        query = highlightQuery,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = SarvDimensions.dimen12),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun ParagraphVerseItem(
    text: String,
    highlightQuery: String,
    modifier: Modifier = Modifier,
) {
    HighlightedText(
        text = text,
        query = highlightQuery,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = SarvDimensions.dimen8),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun SingleVerseItem(
    text: String,
    highlightQuery: String,
    modifier: Modifier = Modifier,
) {
    HighlightedText(
        text = text,
        query = highlightQuery,
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = SarvDimensions.dimen16),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun PoemOrnamentalDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            SarvDimensions.dimen4, Alignment.CenterHorizontally),
    ) {
        Image(
            modifier = Modifier.size(SarvDimensions.dimen32),
            painter = painterResource(Res.drawable.ornoment230r),
            contentDescription = null,
        )
        Image(
            modifier = Modifier.size(SarvDimensions.dimen48),
            painter = painterResource(Res.drawable.ornoment30),
            contentDescription = null,
        )
        Image(
            modifier = Modifier.size(SarvDimensions.dimen32),
            painter = painterResource(Res.drawable.ornoment230l),
            contentDescription = null,
        )
    }
}

@Preview
@Composable
private fun PoemOrnamentalDividerPreview() {
    SarvTheme {
        PoemOrnamentalDivider()
    }
}

@Composable
fun PoemActionBar(
    isLiked: Boolean,
    isProse: Boolean,
    onSearchClick: () -> Unit,
    onShareClick: () -> Unit,
    onLikeClick: () -> Unit,
    onImageCreatorClick: () -> Unit,
    onMemorizeClick: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
) {
    if (isExpanded) {
        Column(
            modifier =
                modifier
                    .clip(RoundedCornerShape(SarvDimensions.dimen16))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = SarvDimensions.dimen1,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(SarvDimensions.dimen16),
                    ).padding(vertical = SarvDimensions.dimen8, horizontal = SarvDimensions.dimen6),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PoemActionItem(
                icon = if (isLiked) Res.drawable.heart_filled else Res.drawable.heart,
                label = Res.string.poem_liked,
                contentDescription = Res.string.cd_like,
                tint = if (isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                labelColor = if (isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = onLikeClick,
            )

            PoemActionItem(
                icon = Res.drawable.palette,
                label = Res.string.poem_image_creator,
                contentDescription = Res.string.cd_image_creator,
                onClick = onImageCreatorClick,
            )

            if (!isProse) {
                PoemActionItem(
                    icon = Res.drawable.add_box_24px,
                    label = Res.string.poem_memorize,
                    contentDescription = Res.string.cd_add_poem,
                    onClick = onMemorizeClick,
                    tint = MaterialTheme.colorScheme.primary,
                    labelColor = MaterialTheme.colorScheme.primary,
                )
            }

            PoemActionItem(
                icon = Res.drawable.share,
                label = Res.string.poem_share,
                contentDescription = Res.string.cd_share,
                onClick = onShareClick,
            )

            PoemActionItem(
                icon = Res.drawable.search,
                label = Res.string.search,
                contentDescription = Res.string.cd_context_search,
                onClick = onSearchClick,
            )
        }
    } else {
        Row(
            modifier =
                modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(SarvDimensions.dimen16))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = SarvDimensions.dimen1,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(SarvDimensions.dimen16),
                    ).padding(vertical = SarvDimensions.dimen8, horizontal = SarvDimensions.dimen6),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {

            PoemActionItem(
                icon = if (isLiked) Res.drawable.heart_filled else Res.drawable.heart,
                label = Res.string.poem_liked,
                contentDescription = Res.string.cd_like,
                tint = if (isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                labelColor = if (isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = onLikeClick,
            )


            PoemActionItem(
                icon = Res.drawable.palette,
                label = Res.string.poem_image_creator,
                contentDescription = Res.string.cd_image_creator,
                onClick = onImageCreatorClick,
            )


            if (!isProse) {
                PoemActionItem(
                    icon = Res.drawable.add_box_24px,
                    label = Res.string.poem_memorize,
                    contentDescription = Res.string.cd_add_poem,
                    onClick = onMemorizeClick,
                    tint = MaterialTheme.colorScheme.primary,
                    labelColor = MaterialTheme.colorScheme.primary,
                )
            }

            PoemActionItem(
                icon = Res.drawable.share,
                label = Res.string.poem_share,
                contentDescription = Res.string.cd_share,
                onClick = onShareClick,
            )

            PoemActionItem(
                icon = Res.drawable.search,
                label = Res.string.search,
                contentDescription = Res.string.cd_context_search,
                onClick = onSearchClick,
            )
        }
    }
}

@Composable
private fun PoemActionItem(
    icon: DrawableResource,
    label: StringResource,
    contentDescription: StringResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Column(
        modifier =
            modifier
                .width(SarvDimensions.dimen64)
                .clip(RoundedCornerShape(SarvDimensions.dimen12))
                .clickable(onClick = onClick)
                .padding(vertical = SarvDimensions.dimen4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SarvDimensions.dimen6),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = stringResource(contentDescription),
            tint = tint,
            modifier = Modifier.size(SarvDimensions.dimen22),
        )
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
            textAlign = TextAlign.Center,
        )
    }
}
