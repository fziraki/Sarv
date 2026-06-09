package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.domain.model.RandomDistich
import com.azbarkon.db.SelectRandomDistichAtOffset
import com.azbarkon.db.SelectRandomDistichAtOffsetByPoet

fun SelectRandomDistichAtOffset.toRandomDistich(): RandomDistich =
    RandomDistich(
        poemId = poem_id.toInt(),
        vorder = vorder.toInt(),
        rightText = right_text,
        leftText = left_text,
        poetName = poet_name,
    )

fun SelectRandomDistichAtOffsetByPoet.toRandomDistich(): RandomDistich =
    RandomDistich(
        poemId = poem_id.toInt(),
        vorder = vorder.toInt(),
        rightText = right_text,
        leftText = left_text,
        poetName = poet_name,
    )
