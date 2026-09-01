package abkabk.azbarkon.ui.components

import abkabk.azbarkon.ui.theme.SarvTheme
import androidx.compose.foundation.layout.height
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
import abkabk.azbarkon.core.designsystem.LocalSarvDimensions

@Composable
fun SarvPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    SarvButton(
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
fun SarvButton(
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
        shape = SarvButtonDefaults.Shape,
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
private fun SarvPrimaryButtonPreview() {
    SarvTheme {
        SarvPrimaryButton(
            text = "تمرین حفظ این شعر",
            onClick = {},
            modifier = Modifier.height(LocalSarvDimensions.current.dimen52),
        )
    }
}
