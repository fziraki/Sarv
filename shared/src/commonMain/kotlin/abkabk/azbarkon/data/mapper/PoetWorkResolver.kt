package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.domain.model.CatNode
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.PoetWork

private val sectionKeywords =
    setOf(
        "غزلیات",
        "قطعات",
        "رباعیات",
        "قصاید",
        "دیوان اشعار",
        "اشعار منتسب",
        "مواعظ",
        "رسائل",
    )

fun resolvePoetWorks(
    poet: Poet,
    rootChildren: List<CatNode>,
): List<PoetWork> {
    val rootCatId = poet.rootCatId ?: return emptyList()
    if (rootChildren.isEmpty()) return emptyList()

    return if (shouldTreatRootAsSingleWork(rootChildren)) {
        listOf(
            PoetWork(
                id = rootCatId,
                title = singleWorkTitle(poet),
                subtitle = rootChildren.joinToString(" • ") { it.text },
            ),
        )
    } else {
        rootChildren.map { child ->
            PoetWork(
                id = child.id,
                title = child.text,
                subtitle = null,
            )
        }
    }
}

fun worksSummary(works: List<PoetWork>): String =
    works.joinToString(" • ") { it.title }

private fun shouldTreatRootAsSingleWork(rootChildren: List<CatNode>): Boolean {
    if (rootChildren.size <= 1) {
        return rootChildren.singleOrNull()?.let { isSectionName(it.text) } == true
    }
    return rootChildren.all { isSectionName(it.text) }
}

private fun isSectionName(text: String): Boolean {
    val normalized = text.trim()
    if (sectionKeywords.any { normalized.contains(it) }) return true
    return normalized.startsWith("رباعیات")
}

private fun singleWorkTitle(poet: Poet): String {
    val poetName = poet.name?.trim().orEmpty()
    if (poetName.isEmpty()) return "دیوان"
    val displayName = poetName.split(' ').firstOrNull()?.ifBlank { poetName } ?: poetName
    return "دیوان $displayName"
}
