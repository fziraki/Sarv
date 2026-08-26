package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.domain.model.CatNode

fun com.sarv.db.Cat.toCatNode(): CatNode =
    CatNode(
        id = id.toInt(),
        poetId = poet_id.toInt(),
        text = text,
        parentId = parent_id.toInt(),
        url = url,
    )
