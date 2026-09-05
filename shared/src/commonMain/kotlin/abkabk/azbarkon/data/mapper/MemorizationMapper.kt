package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.domain.model.memorization.ActiveMemorizationStatus
import abkabk.azbarkon.domain.model.memorization.SrsCard
import abkabk.azbarkon.domain.model.memorization.SrsGrade
import com.azbarkon.memorization.Srs_poem_card

fun Srs_poem_card.toSrsCard(): SrsCard =
    SrsCard(
        id = id,
        poemId = poem_id.toInt(),
        cardIndex = card_index.toInt(),
        front = front,
        back = back,
        interval = interval.toInt(),
        dueDateMillis = due_date,
        consecutiveCorrect = consecutive_correct.toInt(),
        score = score,
    )

fun SrsGrade.toStorageValue(): String = name

fun String.toSrsGrade(): SrsGrade = SrsGrade.valueOf(this)

fun String.toActiveMemorizationStatus(): ActiveMemorizationStatus =
    ActiveMemorizationStatus.valueOf(this)
