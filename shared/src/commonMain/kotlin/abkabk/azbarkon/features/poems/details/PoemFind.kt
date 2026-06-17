package abkabk.azbarkon.features.poems.details

fun findFirstMatchingVerse(
    verses: List<PoemVerseUi>,
    query: String,
): PoemVerseUi? {
    val trimmedQuery = query.trim()
    if (trimmedQuery.isEmpty()) return null

    return verses.firstOrNull { verse ->
        verse.text.isNotBlank() && verse.text.contains(trimmedQuery, ignoreCase = true)
    }
}
