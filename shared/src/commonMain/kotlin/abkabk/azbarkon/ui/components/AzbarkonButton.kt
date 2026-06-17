package abkabk.azbarkon.ui.components

import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

object AzbarkonButtonDefaults {
    val Shape = RoundedCornerShape(12.dp)
}

@Composable
fun AzbarkonPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    AzbarkonButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        textStyle = textStyle,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primary,
                disabledContentColor = MaterialTheme.colorScheme.onPrimary,
            ),
    )
}

@Composable
fun AzbarkonSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
) {
    AzbarkonButton(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        textStyle = textStyle,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                disabledContentColor = MaterialTheme.colorScheme.primary,
            ),
    )
}

@Composable
fun AzbarkonButton(
    text: String,
    onClick: () -> Unit,
    colors: ButtonColors,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = AzbarkonButtonDefaults.Shape,
        colors = colors,
    ) {
        Text(
            text = text,
            style = textStyle,
        )
    }
}

@Preview
@Composable
private fun AzbarkonPrimaryButtonPreview() {
    AzbarkonTheme {
        AzbarkonPrimaryButton(
            text = "تمرین حفظ این شعر",
            onClick = {},
            modifier = Modifier.height(52.dp),
        )
    }
}

@Preview
@Composable
private fun AzbarkonSecondaryButtonPreview() {
    AzbarkonTheme {
        AzbarkonSecondaryButton(
            text = "مشاهده آثار",
            onClick = {},
        )
    }
}
