package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.domain.model.CatNode

fun rootCategoriesSummary(
    categories: List<CatNode>,
    formatOthers: (firstCategory: String, otherCount: Int) -> String = { first, count ->
        "$first و $count اثر دیگر"
    },
): String {
    if (categories.isEmpty()) return ""
    val label = categories.minBy { it.text.length }.text
    val otherCount = categories.size - 1
    return if (otherCount == 0) {
        label
    } else {
        formatOthers(label, otherCount)
    }
}
