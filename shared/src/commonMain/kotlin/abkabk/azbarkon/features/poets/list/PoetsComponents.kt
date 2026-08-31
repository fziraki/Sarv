package abkabk.azbarkon.features.poets.list

import abkabk.azbarkon.ui.components.NetworkImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import sarv.shared.generated.resources.Res
import sarv.shared.generated.resources.filter
import sarv.shared.generated.resources.search
import sarv.shared.generated.resources.unknown
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import abkabk.azbarkon.core.designsystem.SarvDimensions


@Composable
fun FilterField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(SarvDimensions.dimen14))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = SarvDimensions.dimen1,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(SarvDimensions.dimen14),
                ).padding(horizontal = SarvDimensions.dimen14, vertical = SarvDimensions.dimen12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SarvDimensions.dimen10),
    ) {
        Icon(
            painter = painterResource(Res.drawable.filter),
            contentDescription = stringResource(Res.string.search),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(SarvDimensions.dimen20),
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            textStyle =
                MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start,
                ),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}

@Composable
fun PoetAvatar(
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    if (imageUrl != null) {
        NetworkImage(
            modifier = modifier.clip(CircleShape),
            imageUrl = imageUrl,
        )
    } else {
        Image(
            modifier =
                modifier.clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.primary)
                    .padding(SarvDimensions.dimen8),
            painter = painterResource(Res.drawable.unknown),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.surface)
        )
    }
}

@Composable
fun PoetsSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier.fillMaxWidth(),
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Start,
    )
}
