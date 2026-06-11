package abkabk.azbarkon.domain.srs

import abkabk.azbarkon.core.util.currentTimeMillis
import abkabk.azbarkon.domain.model.PoemVerse
import abkabk.azbarkon.domain.model.memorization.SrsCard
import abkabk.azbarkon.domain.srs.SrsScheduler.DEFAULT_EASE

data class GeneratedCard(
    val cardIndex: Int,
    val front: String,
    val back: String,
)

object CardGenerator {
    fun generateCards(
        poemId: Int,
        verses: List<PoemVerse>,
        nowMillis: Long = currentTimeMillis(),
    ): List<SrsCard> {
        val generated = buildGeneratedCards(verses)
        return generated.map { card ->
            SrsCard(
                id = 0,
                poemId = poemId,
                cardIndex = card.cardIndex,
                front = card.front,
                back = card.back,
                interval = 0,
                ease = DEFAULT_EASE,
                dueDateMillis = nowMillis,
                consecutiveCorrect = 0,
            )
        }
    }

    fun buildGeneratedCards(verses: List<PoemVerse>): List<GeneratedCard> {
        if (verses.isEmpty()) return emptyList()

        val grouped =
            verses
                .sortedWith(compareBy({ it.vorder }, { it.position }))
                .groupBy { it.vorder }

        return grouped.entries
            .sortedBy { it.key }
            .mapIndexed { index, (_, coupletVerses) ->
                val sortedCouplet = coupletVerses.sortedBy { it.position }
                val firstLine = sortedCouplet.firstOrNull()?.text.orEmpty()
                val fullBack =
                    sortedCouplet.joinToString("\n") { it.text }.trim()

                val front =
                    if (sortedCouplet.size > 1) {
                        "$firstLine\n..."
                    } else {
                        maskWords(firstLine)
                    }

                GeneratedCard(
                    cardIndex = index,
                    front = front,
                    back = fullBack,
                )
            }
    }

    private fun maskWords(line: String): String {
        val words = line.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (words.size <= 2) return line
        val visibleCount = (words.size / 2).coerceAtLeast(1)
        return words.take(visibleCount).joinToString(" ") + " ..."
    }
}
