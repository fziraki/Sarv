package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.domain.model.MyPoemSummary
import abkabk.azbarkon.domain.model.PoemDetail
import abkabk.azbarkon.domain.model.PoemSummary
import abkabk.azbarkon.domain.model.PoemVerse

fun com.sarv.db.SelectByCatIdPaged.toPoemSummary(): PoemSummary =
    PoemSummary(
        id = id.toInt(),
        title = title,
    )

fun com.sarv.db.SelectByIds.toMyPoemSummary(): MyPoemSummary =
    MyPoemSummary(
        id = id.toInt(),
        title = title,
        poetName = poet_name,
        categoryName = category_name,
    )

fun com.sarv.db.Verse.toPoemVerse(poemId: Int): PoemVerse =
    PoemVerse(
        poemId = poemId,
        vorder = vorder.toInt(),
        position = position.toInt(),
        text = text,
    )

fun List<com.sarv.db.Verse>.toPoemVerses(poemId: Int): List<PoemVerse> =
    sortedWith(compareBy({ it.vorder }, { it.position }))
        .map { it.toPoemVerse(poemId) }

fun com.sarv.db.SelectDetailById.toPoemDetail(
    poemId: Int,
    verses: List<PoemVerse>,
): PoemDetail =
    PoemDetail(
        id = poemId,
        title = title,
        poetName = poet_name,
        categoryName = category_name,
        verses = verses,
    )
