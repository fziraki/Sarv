package abkabk.azbarkon.features.poems.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.cd_copy
import azbarkoncmp.shared.generated.resources.cd_image_creator
import azbarkoncmp.shared.generated.resources.cd_like
import azbarkoncmp.shared.generated.resources.cd_share
import azbarkoncmp.shared.generated.resources.copy
import azbarkoncmp.shared.generated.resources.heart
import azbarkoncmp.shared.generated.resources.heart_filled
import azbarkoncmp.shared.generated.resources.palette
import azbarkoncmp.shared.generated.resources.poem_copy
import azbarkoncmp.shared.generated.resources.poem_image_creator
import azbarkoncmp.shared.generated.resources.poem_liked
import azbarkoncmp.shared.generated.resources.poem_share
import azbarkoncmp.shared.generated.resources.share
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PoemVerseItem(
    verse: PoemVerseUi,
    modifier: Modifier = Modifier,
) {
    when (verse.positionType) {
        PoemVersePositionType.Comment -> CommentVerseItem(text = verse.text, modifier = modifier)
        PoemVersePositionType.Right -> RightVerseItem(text = verse.text, modifier = modifier)
        PoemVersePositionType.Left -> LeftVerseItem(text = verse.text, modifier = modifier)
        PoemVersePositionType.Center -> CenterVerseItem(text = verse.text, modifier = modifier)
        PoemVersePositionType.Paragraph -> ParagraphVerseItem(text = verse.text, modifier = modifier)
        PoemVersePositionType.Single -> SingleVerseItem(text = verse.text, modifier = modifier)
    }
}

@Composable
private fun CommentVerseItem(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 10.dp),
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun RightVerseItem(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Start,
    )
}

@Composable
private fun LeftVerseItem(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.End,
    )
}

@Composable
private fun CenterVerseItem(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun ParagraphVerseItem(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun SingleVerseItem(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        text = text,
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
        horizontalArrangement = Arrangement.Center,
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        Box(
            modifier =
                Modifier
                    .padding(horizontal = 12.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline),
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

@Composable
fun PoemActionBar(
    isLiked: Boolean,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
    onLikeClick: () -> Unit,
    onImageCreatorClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp),
                ).padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        PoemActionItem(
            icon = Res.drawable.palette,
            label = Res.string.poem_image_creator,
            contentDescription = Res.string.cd_image_creator,
            onClick = onImageCreatorClick,
        )

        PoemActionItem(
            icon = if (isLiked) Res.drawable.heart_filled else Res.drawable.heart,
            label = Res.string.poem_liked,
            contentDescription = Res.string.cd_like,
            tint = if (isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            labelColor = if (isLiked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = onLikeClick,
        )

        PoemActionItem(
            icon = Res.drawable.share,
            label = Res.string.poem_share,
            contentDescription = Res.string.cd_share,
            onClick = onShareClick,
        )

        PoemActionItem(
            icon = Res.drawable.copy,
            label = Res.string.poem_copy,
            contentDescription = Res.string.cd_copy,
            onClick = onCopyClick,
        )
    }
}

@Composable
private fun PoemActionItem(
    icon: DrawableResource,
    label: StringResource,
    contentDescription: StringResource,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    labelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .width(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick)
                .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = stringResource(contentDescription),
            tint = tint,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
            textAlign = TextAlign.Center,
        )
    }
}
