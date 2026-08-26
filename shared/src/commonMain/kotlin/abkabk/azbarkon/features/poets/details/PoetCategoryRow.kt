package abkabk.azbarkon.features.poets.details

import abkabk.azbarkon.features.poets.PoetCategoryRowUi
import abkabk.azbarkon.ui.theme.SarvTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sarv.shared.generated.resources.Res
import sarv.shared.generated.resources.cd_expand_category
import sarv.shared.generated.resources.maktab
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun PoetCategoryRow(
    category: PoetCategoryRowUi,
    onToggleClick: () -> Unit,
    onLeafClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isChildRow = category.depth > 0
    val backgroundColor =
        if (isChildRow) {
            MaterialTheme.colorScheme.secondary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    val contentColor =
        if (isChildRow) {
            MaterialTheme.colorScheme.onSecondary
        } else {
            MaterialTheme.colorScheme.onBackground
        }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .then(
                    if (isChildRow) {
                        Modifier.padding(horizontal = (category.depth * 16 + 12).dp)
                    } else {
                        Modifier
                    },
                )
                .clip(RoundedCornerShape(16.dp))
                .background(backgroundColor)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(16.dp),
                ).clickable(
                    onClick = {
                        if (category.isParent) {
                            onToggleClick()
                        } else {
                            onLeafClick()
                        }
                    },
                ).padding(14.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = category.title,
                style = MaterialTheme.typography.headlineMedium,
                color = contentColor,
                textAlign = TextAlign.Center,
            )

            if (category.isParent) {
                Icon(
                    painter = painterResource(Res.drawable.maktab),
                    contentDescription = stringResource(Res.string.cd_expand_category),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier =
                        Modifier
                            .align(Alignment.CenterStart)
                            .size(24.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun PoetCategoryRowPreview() {
    SarvTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PoetCategoryRow(
                category =
                    PoetCategoryRowUi(
                        id = 24,
                        title = "غزلیات",
                        depth = 0,
                        isParent = true,
                        isExpanded = true,
                    ),
                onToggleClick = {},
                onLeafClick = {},
            )
            PoetCategoryRow(
                category =
                    PoetCategoryRowUi(
                        id = 100,
                        title = "غزل ۱",
                        depth = 1,
                        isParent = false,
                        isExpanded = false,
                    ),
                onToggleClick = {},
                onLeafClick = {},
            )
        }
    }
}
