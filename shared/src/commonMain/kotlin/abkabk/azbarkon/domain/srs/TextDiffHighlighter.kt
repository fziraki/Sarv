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
    private const val EASY_THRESHOLD = 0.95
    private const val GOOD_THRESHOLD = 0.75
    private const val HARD_THRESHOLD = 0.45

    fun normalizeForComparison(text: String): String {
        val withoutSpecials = text.replace('\u0640', ' ').replace('\u200C', ' ')
        val builder = StringBuilder(withoutSpecials.length)
        withoutSpecials.forEach { char ->
            if (char.category != CharCategory.NON_SPACING_MARK) {
                builder.append(normalizeArabicLetter(char))
            }
        }
        return builder.toString()
    }

    private fun normalizeArabicLetter(char: Char): Char =
        when (char) {
            '\u0622', '\u0623', '\u0625' -> '\u0627'
            '\u064A' -> '\u06CC'
            '\u0643' -> '\u06A9'
            '\u0629' -> '\u0647'
            else -> char
        }

    fun extractAlphabeticLetters(text: String): String {
        val builder = StringBuilder()
        normalizeForComparison(text).forEach { char ->
            if (char.isLetter()) {
                builder.append(char)
            }
        }
        return builder.toString()
    }

    fun charMatchRatio(expected: String, actual: String): Double {
        val expectedLetters = extractAlphabeticLetters(expected)
        val actualLetters = extractAlphabeticLetters(actual)
        if (expectedLetters.isEmpty()) return 1.0
        val lcsLength = longestCommonSubsequenceLength(expectedLetters, actualLetters, ignoreCase = true)
        return lcsLength.toDouble() / expectedLetters.length
    }

    fun suggestGradeFromChars(
        expected: String,
        actual: String,
    ): abkabk.azbarkon.domain.model.memorization.SrsGrade {
        val ratio = charMatchRatio(expected, actual)
        return when {
            ratio >= EASY_THRESHOLD -> abkabk.azbarkon.domain.model.memorization.SrsGrade.EASY
            ratio >= GOOD_THRESHOLD -> abkabk.azbarkon.domain.model.memorization.SrsGrade.GOOD
            ratio >= HARD_THRESHOLD -> abkabk.azbarkon.domain.model.memorization.SrsGrade.HARD
            else -> abkabk.azbarkon.domain.model.memorization.SrsGrade.AGAIN
        }
    }

    fun diffUserWords(expected: String, actual: String): List<DiffToken> {
        val expectedLetters = extractAlphabeticLetters(expected)
        val actualWords = splitDisplayWords(actual)
        if (actualWords.isEmpty()) return emptyList()

        var expectedIndex = 0
        return actualWords.map { word ->
            val wordLetters = extractAlphabeticLetters(word)
            val matched =
                wordLetters.isNotEmpty() &&
                    expectedIndex + wordLetters.length <= expectedLetters.length &&
                    expectedLetters
                        .substring(expectedIndex, expectedIndex + wordLetters.length)
                        .equals(wordLetters, ignoreCase = true)
            if (wordLetters.isNotEmpty()) {
                expectedIndex += wordLetters.length
            }
            DiffToken(word, if (matched) DiffTokenType.CORRECT else DiffTokenType.WRONG)
        }
    }

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
            ratio >= EASY_THRESHOLD -> abkabk.azbarkon.domain.model.memorization.SrsGrade.EASY
            ratio >= GOOD_THRESHOLD -> abkabk.azbarkon.domain.model.memorization.SrsGrade.GOOD
            ratio >= HARD_THRESHOLD -> abkabk.azbarkon.domain.model.memorization.SrsGrade.HARD
            else -> abkabk.azbarkon.domain.model.memorization.SrsGrade.AGAIN
        }
    }

    private fun splitDisplayWords(text: String): List<String> =
        text
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

    private fun tokenize(text: String): List<String> =
        normalizeForComparison(text)
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

    private fun longestCommonSubsequenceLength(
        first: String,
        second: String,
        ignoreCase: Boolean,
    ): Int {
        val m = first.length
        val n = second.length
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 1..m) {
            for (j in 1..n) {
                dp[i][j] =
                    if (charsMatch(first[i - 1], second[j - 1], ignoreCase)) {
                        dp[i - 1][j - 1] + 1
                    } else {
                        maxOf(dp[i - 1][j], dp[i][j - 1])
                    }
            }
        }
        return dp[m][n]
    }

    private fun charsMatch(
        first: Char,
        second: Char,
        ignoreCase: Boolean,
    ): Boolean = if (ignoreCase) first.equals(second, ignoreCase = true) else first == second

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
                    if (wordsMatch(expected[i - 1], actual[j - 1])) {
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
                i > 0 && j > 0 && wordsMatch(expected[i - 1], actual[j - 1]) -> {
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

    private fun wordsMatch(expected: String, actual: String): Boolean =
        expected.equals(actual, ignoreCase = true)
}
