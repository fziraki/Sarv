package abkabk.azbarkon.domain.srs

enum class DiffTokenType {
    CORRECT,
    MISSING,
    WRONG,
}

data class DiffToken(
    val text: String,
    val type: DiffTokenType,
    val coveredWords: Int = 1,
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

    fun suggestGradeFromChars(
        expected: String,
        actual: String,
    ): abkabk.azbarkon.domain.model.memorization.SrsGrade {
        val tokens = diffUserWords(expected, actual)
        val expectedWords = splitDisplayWords(expected)
        if (expectedWords.isEmpty()) return abkabk.azbarkon.domain.model.memorization.SrsGrade.EASY
        val covered = tokens.filter { it.type == DiffTokenType.CORRECT }.sumOf { it.coveredWords }
        val ratio = covered.toDouble() / expectedWords.size
        return when {
            ratio >= EASY_THRESHOLD -> abkabk.azbarkon.domain.model.memorization.SrsGrade.EASY
            ratio >= GOOD_THRESHOLD -> abkabk.azbarkon.domain.model.memorization.SrsGrade.GOOD
            ratio >= HARD_THRESHOLD -> abkabk.azbarkon.domain.model.memorization.SrsGrade.HARD
            else -> abkabk.azbarkon.domain.model.memorization.SrsGrade.AGAIN
        }
    }

    fun diffUserWords(expected: String, actual: String): List<DiffToken> {
        val expectedWords = splitDisplayWords(expected)
        val actualWords = splitDisplayWords(actual)
        if (actualWords.isEmpty()) return emptyList()

        val tokens = mutableListOf<DiffToken>()
        var expectedIndex = 0
        actualWords.forEach { word ->
            val wordLetters = extractAlphabeticLetters(word)
            if (wordLetters.isEmpty()) {
                tokens.add(DiffToken(word, DiffTokenType.WRONG))
            } else {
                val covered = matchSpan(expectedWords, wordLetters, expectedIndex)
                if (covered > 0) {
                    tokens.add(DiffToken(word, DiffTokenType.CORRECT, covered))
                    expectedIndex += covered
                } else {
                    val skipped =
                        (expectedIndex + 1..expectedWords.lastIndex).firstOrNull { i ->
                            matchSpan(expectedWords, wordLetters, i) > 0
                        }
                    if (skipped != null) {
                        (expectedIndex until skipped).forEach { i ->
                            tokens.add(DiffToken(expectedWords[i], DiffTokenType.MISSING))
                        }
                        val skippedCovered = matchSpan(expectedWords, wordLetters, skipped)
                        tokens.add(DiffToken(word, DiffTokenType.CORRECT, skippedCovered))
                        expectedIndex = skipped + skippedCovered
                    } else {
                        tokens.add(DiffToken(word, DiffTokenType.WRONG))
                        if (expectedIndex < expectedWords.size) expectedIndex++
                    }
                }
            }
        }
        if (expectedIndex < expectedWords.size) {
            (expectedIndex until expectedWords.size).forEach { i ->
                tokens.add(DiffToken(expectedWords[i], DiffTokenType.MISSING))
            }
        }
        return tokens
    }

    private fun matchSpan(expectedWords: List<String>, wordLetters: String, index: Int): Int {
        if (index >= expectedWords.size) return 0
        for (length in 1..3) {
            val end = index + length
            if (end > expectedWords.size) break
            val span = expectedWords.subList(index, end).joinToString("") { extractAlphabeticLetters(it) }
            if (span.equals(wordLetters, ignoreCase = true)) return length
        }
        return 0
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
            .replace('\u0640', ' ')
            .replace('\u200C', ' ')
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }

    private fun tokenize(text: String): List<String> =
        normalizeForComparison(text)
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
