package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.domain.model.SearchHit

fun com.sarv.db.SearchVerses.toSearchHit(): SearchHit =
    SearchHit(
        poemId = poem_id.toInt(),
        poemTitle = poem_title,
        poetName = poet_name,
        categoryName = category_name,
        verseText = verse_text,
        verseOrder = verse_order.toInt(),
    )
