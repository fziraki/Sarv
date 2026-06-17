package abkabk.azbarkon.features.poems.details

import abkabk.azbarkon.domain.model.PoemVerse
import androidx.compose.runtime.Stable

enum class PoemVersePositionType {
    Comment,
    Right,
    Left,
    Center,
    Paragraph,
    Single,
}

fun Int.toPoemVersePositionType(): PoemVersePositionType =
    when (this) {
        -1 -> PoemVersePositionType.Comment
        0 -> PoemVersePositionType.Right
        1 -> PoemVersePositionType.Left
        2 -> PoemVersePositionType.Center
        3 -> PoemVersePositionType.Paragraph
        else -> PoemVersePositionType.Single
    }

@Stable
data class PoemVerseUi(
    val id: String,
    val text: String,
    val positionType: PoemVersePositionType,
)

fun PoemVerse.toPoemVerseUi(): PoemVerseUi =
    PoemVerseUi(
        id = "$vorder-$position",
        text = text,
        positionType = position.toPoemVersePositionType(),
    )
