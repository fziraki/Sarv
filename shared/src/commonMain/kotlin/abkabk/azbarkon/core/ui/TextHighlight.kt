package abkabk.azbarkon.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle

fun buildHighlightedAnnotatedString(
    text: String,
    query: String,
    highlightStyle: SpanStyle,
): AnnotatedString {
    val trimmedQuery = query.trim()
    if (trimmedQuery.isEmpty()) {
        return AnnotatedString(text)
    }

    return buildAnnotatedString {
        var startIndex = 0
        while (startIndex < text.length) {
            val matchIndex = text.indexOf(trimmedQuery, startIndex, ignoreCase = true)
            if (matchIndex < 0) {
                append(text.substring(startIndex))
                break
            }

            if (matchIndex > startIndex) {
                append(text.substring(startIndex, matchIndex))
            }

            withStyle(highlightStyle) {
                append(text.substring(matchIndex, matchIndex + trimmedQuery.length))
            }

            startIndex = matchIndex + trimmedQuery.length
        }
    }
}

@Composable
fun HighlightedText(
    text: String,
    query: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onBackground,
    textAlign: TextAlign? = null,
) {
    val annotatedText =
        buildHighlightedAnnotatedString(
            text = text,
            query = query,
            highlightStyle = SpanStyle(color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold),
        )

    Text(
        text = annotatedText,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign,
    )
}
