package abkabk.azbarkon.features.chat

private val PERSIAN_PUNCTUATION = Regex("[؟!،؛,ءًٌٍَُِئأإؤ]")
private val NON_ARABIC_SCRIPT = Regex("[^؀-ۿ]")

fun extractLastPersianLetter(message: String): Char? {
    val pure =
        message
            .replace(Regex("\\s+$"), "")
            .replace(NON_ARABIC_SCRIPT, "")
            .replace(PERSIAN_PUNCTUATION, "")

    return pure.lastOrNull()
}
