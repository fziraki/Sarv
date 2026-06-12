package abkabk.azbarkon.data.generator

import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.games.GameConstants
import abkabk.azbarkon.domain.model.games.GameQuestion
import abkabk.azbarkon.domain.model.games.OrganizeLine
import abkabk.azbarkon.domain.model.games.PoetOption
import kotlin.random.Random

internal object GameQuestionGenerator {
    fun buildNextVerseQuestion(
        promptLine: String,
        poetName: String,
        correctAnswer: String,
        distractors: List<String>,
        seed: Long,
    ): GameQuestion.NextVerse? {
        val options =
            buildOptions(correctAnswer, distractors, seed) ?: return null
        val correctIndex = options.indexOf(correctAnswer)
        if (correctIndex < 0) return null
        return GameQuestion.NextVerse(
            promptLine = promptLine,
            poetName = poetName,
            options = options,
            correctIndex = correctIndex,
        )
    }

    fun buildFindPoetQuestion(
        line1: String,
        line2: String,
        correctPoet: Poet,
        allPoets: List<Poet>,
        seed: Long,
    ): GameQuestion.FindPoet? {
        val correctId = correctPoet.id ?: return null
        val distractorPoets =
            allPoets
                .filter { it.id != null && it.id != correctId && !it.name.isNullOrBlank() }
                .shuffled(Random(seed))
                .take(3)
        if (distractorPoets.size < 3) return null
        val options =
            (listOf(correctPoet) + distractorPoets)
                .map { poet ->
                    PoetOption(
                        id = poet.id!!,
                        name = poet.name.orEmpty(),
                        imageUrl = poet.imageUrl,
                    )
                }.shuffled(Random(seed + 1))
        return GameQuestion.FindPoet(
            line1 = line1,
            line2 = line2,
            options = options,
            correctPoetId = correctId,
        )
    }

    fun buildCompletePoemQuestion(
        line1: String,
        line2: String,
        poemWords: List<String>,
        seed: Long,
    ): GameQuestion.CompletePoem? {
        val line2Words = line2.splitWords()
        if (line2Words.size < 2) return null

        val candidateIndices =
            line2Words.indices.filter { line2Words[it].length >= GameConstants.MIN_WORD_LENGTH }
        if (candidateIndices.size < 2) return null

        val random = Random(seed)
        val selectedIndices = candidateIndices.shuffled(random).take(2).sorted()
        val word1 = line2Words[selectedIndices[0]]
        val word2 = line2Words[selectedIndices[1]]

        val blankedLine2 =
            line2Words
                .mapIndexed { index, word ->
                    if (index == selectedIndices[0] || index == selectedIndices[1]) {
                        "____"
                    } else {
                        word
                    }
                }.joinToString(" ")

        val distractorPool =
            poemWords
                .filter { word ->
                    word.length >= GameConstants.MIN_WORD_LENGTH &&
                        word != word1 &&
                        word != word2
                }.distinct()

        val distractors = distractorPool.shuffled(random).take(2)
        if (distractors.size < 2) return null

        val options = (listOf(word1, word2) + distractors).shuffled(random)
        if (options.toSet().size < 4) return null

        return GameQuestion.CompletePoem(
            line1 = line1,
            blankedLine2 = blankedLine2,
            options = options,
            correctWords = word1 to word2,
        )
    }

    fun buildOrganizePoemQuestion(
        poemId: Int,
        startVorder: Int,
        lines: List<String>,
        seed: Long,
    ): GameQuestion.OrganizePoem? {
        if (lines.size != 4 || lines.any { it.isBlank() }) return null
        val organizeLines =
            lines.mapIndexed { index, text ->
                OrganizeLine(
                    id = "$poemId-$startVorder-$index",
                    text = text,
                )
            }
        val correctOrder = organizeLines.map { it.id }
        val shuffled =
            organizeLines
                .shuffled(Random(seed))
                .let { shuffledLines ->
                    if (shuffledLines.map { it.id } == correctOrder) {
                        shuffledLines.reversed()
                    } else {
                        shuffledLines
                    }
                }
        return GameQuestion.OrganizePoem(
            lines = shuffled,
            correctOrder = correctOrder,
        )
    }

    private fun buildOptions(
        correct: String,
        distractors: List<String>,
        seed: Long,
    ): List<String>? {
        val uniqueDistractors =
            distractors
                .filter { it.isNotBlank() && it != correct }
                .distinct()
                .take(3)
        if (uniqueDistractors.size < 3) return null
        return (listOf(correct) + uniqueDistractors).shuffled(Random(seed))
    }

    private fun String.splitWords(): List<String> =
        trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
}
