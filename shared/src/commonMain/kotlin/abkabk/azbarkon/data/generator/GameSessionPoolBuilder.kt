package abkabk.azbarkon.data.generator

import abkabk.azbarkon.domain.model.games.GameConstants
import abkabk.azbarkon.domain.model.games.GameDistichCandidate
import abkabk.azbarkon.domain.model.games.GameOrganizeWindow
import abkabk.azbarkon.domain.model.games.GameType

internal data class VerseRow(
    val vorder: Long,
    val position: Long,
    val text: String,
)

internal data class PoemPoolExtraction(
    val distichs: List<GameDistichCandidate>,
    val organizeWindows: List<GameOrganizeWindow>,
    val poemWords: List<String>,
)

internal object GameSessionPoolBuilder {
    fun isPoemAcceptable(
        gameType: GameType,
        distichCount: Int,
        organizeCount: Int,
    ): Boolean =
        when (gameType) {
            GameType.ORGANIZE_POEM -> organizeCount > 0
            GameType.NEXT_VERSE,
            GameType.FIND_POET,
            GameType.COMPLETE_POEM,
            -> distichCount >= GameConstants.POOL_MIN_DISTICHS_PER_POEM
        }

    fun extractFromVerses(
        poemId: Long,
        poetId: Long,
        poetName: String,
        verses: List<VerseRow>,
    ): PoemPoolExtraction {
        val poemWords =
            verses
                .flatMap { verse -> verse.text.split(Regex("\\s+")).filter { it.isNotBlank() } }
                .distinct()

        val distichs = extractDistichs(poemId, poetId, poetName, verses)
        val organizeWindows = extractOrganizeWindows(poemId, verses)

        return PoemPoolExtraction(
            distichs = distichs,
            organizeWindows = organizeWindows,
            poemWords = poemWords,
        )
    }

    private fun extractDistichs(
        poemId: Long,
        poetId: Long,
        poetName: String,
        verses: List<VerseRow>,
    ): List<GameDistichCandidate> {
        val firstHemistichByVorder =
            verses
                .filter { it.position == 0L && it.text.length <= GameConstants.MAX_HEMISTICH_LENGTH }
                .associateBy { it.vorder }

        val partnerTextByVorder =
            verses
                .groupBy { it.vorder }
                .mapValues { (_, rows) -> rows.minBy { it.position }.text }

        return firstHemistichByVorder.mapNotNull { (vorder, first) ->
            val second = partnerTextByVorder[vorder + 1] ?: return@mapNotNull null
            if (second.length > GameConstants.MAX_HEMISTICH_LENGTH) return@mapNotNull null
            GameDistichCandidate(
                poemId = poemId,
                vorder = vorder,
                firstHemistich = first.text,
                secondHemistich = second,
                poetId = poetId,
                poetName = poetName,
            )
        }
    }

    private fun extractOrganizeWindows(
        poemId: Long,
        verses: List<VerseRow>,
    ): List<GameOrganizeWindow> {
        val lineByVorder =
            verses
                .groupBy { it.vorder }
                .mapValues { (_, rows) -> rows.minBy { it.position }.text }

        val startVorders =
            lineByVorder.keys
                .filter { vorder ->
                    (0L..3L).all { offset -> lineByVorder.containsKey(vorder + offset) }
                }.sorted()

        return startVorders.mapNotNull { startVorder ->
            val lines =
                (0L..3L).mapNotNull { offset ->
                    lineByVorder[startVorder + offset]
                }
            if (lines.size != 4 || lines.any { it.length > GameConstants.MAX_HEMISTICH_LENGTH }) {
                return@mapNotNull null
            }
            GameOrganizeWindow(
                poemId = poemId.toInt(),
                startVorder = startVorder.toInt(),
                lines = lines,
            )
        }
    }
}
