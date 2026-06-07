package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.domain.model.PoemSummary

fun com.azbarkon.db.SelectByCatId.toPoemSummary(): PoemSummary =
    PoemSummary(
        id = id.toInt(),
        title = title,
    )
