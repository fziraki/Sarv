package abkabk.azbarkon.domain.usecase

class BuildShareTextUseCase {
    operator fun invoke(
        poetName: String,
        subtitle: String,
        verseTexts: List<String>,
        selectedText: String? = null,
    ): String {
        if (verseTexts.isEmpty() && selectedText.isNullOrBlank()) return ""

        val body = if (selectedText.isNullOrBlank()) {
            verseTexts.joinToString("\n")
        } else {
            selectedText
        }

        return buildString {
            if (poetName.isNotBlank()) {
                append(poetName)
            }
            if (subtitle.isNotBlank()) {
                if (isNotEmpty()) append("\n")
                append(subtitle)
            }
            if (isNotEmpty()) append("\n\n")
            append(body)
        }
    }
}
