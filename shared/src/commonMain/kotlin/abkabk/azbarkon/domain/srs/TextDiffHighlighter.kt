package abkabk.azbarkon.domain.srs

enum class DiffTokenType {
    CORRECT,
    MISSING,
    WRONG,
}

data class DiffToken(
    val text: String,
    val type: DiffTokenType,
)

object TextDiffHighlighter {
    fun diff(expected: String, actual: String): List<DiffToken> {
        val expectedWords = tokenize(expected)
        val actualWords = tokenize(actual)
        if (expectedWords.isEmpty() && actualWords.isEmpty()) return emptyList()

        val alignment = alignWords(expectedWords, actualWords)
        return alignment
    }

    fun score(expected: String, actual: String): Double {
        val tokens = diff(expected, actual)
        if (tokens.isEmpty()) return 1.0
        val correct = tokens.count { it.type == DiffTokenType.CORRECT }
        return correct.toDouble() / tokens.size.coerceAtLeast(1)
    }

    fun suggestGrade(expected: String, actual: String): abkabk.azbarkon.domain.model.memorization.SrsGrade {
        val ratio = score(expected, actual)
        return when {
            ratio >= 0.95 -> abkabk.azbarkon.domain.model.memorization.SrsGrade.EASY
            ratio >= 0.75 -> abkabk.azbarkon.domain.model.memorization.SrsGrade.GOOD
            ratio >= 0.45 -> abkabk.azbarkon.domain.model.memorization.SrsGrade.HARD
            else -> abkabk.azbarkon.domain.model.memorization.SrsGrade.AGAIN
        }
    }

    private fun tokenize(text: String): List<String> =
        text
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

    private fun alignWords(
        expected: List<String>,
        actual: List<String>,
    ): List<DiffToken> {
        val m = expected.size
        val n = actual.size
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 1..m) {
            for (j in 1..n) {
                dp[i][j] =
                    if (expected[i - 1].equals(actual[j - 1], ignoreCase = true)) {
                        dp[i - 1][j - 1] + 1
                    } else {
                        maxOf(dp[i - 1][j], dp[i][j - 1])
                    }
            }
        }

        val result = mutableListOf<DiffToken>()
        var i = m
        var j = n
        while (i > 0 || j > 0) {
            when {
                i > 0 && j > 0 && expected[i - 1].equals(actual[j - 1], ignoreCase = true) -> {
                    result.add(DiffToken(expected[i - 1], DiffTokenType.CORRECT))
                    i--
                    j--
                }
                j > 0 && (i == 0 || dp[i][j - 1] >= dp[i - 1][j]) -> {
                    result.add(DiffToken(actual[j - 1], DiffTokenType.WRONG))
                    j--
                }
                i > 0 -> {
                    result.add(DiffToken(expected[i - 1], DiffTokenType.MISSING))
                    i--
                }
            }
        }
        return result.asReversed()
    }
}
