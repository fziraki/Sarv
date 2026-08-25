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
    private const val ORGANIZE_WINDOW_SIZE = 4

    fun isPoemAcceptable(
        gameType: GameType,
        distichCount: Int,
        organizeCount: Int,
        hasParagraphVerses: Boolean,
    ): Boolean {
        if (hasParagraphVerses) return false
        return when (gameType) {
            GameType.ORGANIZE_POEM -> organizeCount > 0
            GameType.NEXT_VERSE,
            GameType.FIND_POET,
            GameType.COMPLETE_POEM,
            -> distichCount >= GameConstants.POOL_MIN_DISTICHS_PER_POEM
        }
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

        val versesByVorder = verses.groupBy { it.vorder }
        val distichs = extractDistichs(poemId, poetId, poetName, versesByVorder)
        val organizeWindows = extractOrganizeWindows(poemId, versesByVorder)

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
        versesByVorder: Map<Long, List<VerseRow>>,
    ): List<GameDistichCandidate> =
        versesByVorder.keys
            .sorted()
            .mapNotNull { vorder ->
                val lines = distichLinesAt(versesByVorder, vorder) ?: return@mapNotNull null
                GameDistichCandidate(
                    poemId = poemId,
                    vorder = vorder,
                    firstHemistich = lines[0],
                    secondHemistich = lines[1],
                    poetId = poetId,
                    poetName = poetName,
                )
            }

    private fun extractOrganizeWindows(
        poemId: Long,
        versesByVorder: Map<Long, List<VerseRow>>,
    ): List<GameOrganizeWindow> {
        val coupletWindows =
            versesByVorder.keys
                .sorted()
                .mapNotNull { startVorder ->
                    val firstDistich = sameVorderDistichLines(versesByVorder, startVorder) ?: return@mapNotNull null
                    val secondDistich =
                        sameVorderDistichLines(versesByVorder, startVorder + 1) ?: return@mapNotNull null
                    GameOrganizeWindow(
                        poemId = poemId.toInt(),
                        startVorder = startVorder.toInt(),
                        lines = firstDistich + secondDistich,
                    )
                }
        if (coupletWindows.isNotEmpty()) return coupletWindows

        val ganjoorWindows =
            versesByVorder.keys
                .sorted()
                .mapNotNull { startVorder ->
                    val firstDistich = ganjoorAlternatingDistichLines(versesByVorder, startVorder)
                        ?: return@mapNotNull null
                    val secondDistich =
                        ganjoorAlternatingDistichLines(versesByVorder, startVorder + 2)
                            ?: return@mapNotNull null
                    GameOrganizeWindow(
                        poemId = poemId.toInt(),
                        startVorder = startVorder.toInt(),
                        lines = firstDistich + secondDistich,
                    )
                }
        if (ganjoorWindows.isNotEmpty()) return ganjoorWindows

        val lineByVorder =
            versesByVorder
                .mapNotNull { (vorder, rows) ->
                    val text =
                        rows.find { it.position == 0L }?.text
                            ?: return@mapNotNull null
                    if (text.length > GameConstants.MAX_HEMISTICH_LENGTH) return@mapNotNull null
                    vorder to text
                }.toMap()

        return lineByVorder.keys
            .sorted()
            .mapNotNull { startVorder ->
                val lines =
                    (0L until ORGANIZE_WINDOW_SIZE.toLong()).mapNotNull { offset ->
                        lineByVorder[startVorder + offset]
                    }
                if (lines.size != ORGANIZE_WINDOW_SIZE) return@mapNotNull null
                GameOrganizeWindow(
                    poemId = poemId.toInt(),
                    startVorder = startVorder.toInt(),
                    lines = lines,
                )
            }
    }

    private fun distichLinesAt(
        versesByVorder: Map<Long, List<VerseRow>>,
        vorder: Long,
    ): List<String>? {
        sameVorderDistichLines(versesByVorder, vorder)?.let { return it }
        ganjoorAlternatingDistichLines(versesByVorder, vorder)?.let { return it }
        return legacyFallbackDistichLines(versesByVorder, vorder)
    }

    private fun legacyFallbackDistichLines(
        versesByVorder: Map<Long, List<VerseRow>>,
        vorder: Long,
    ): List<String>? {
        if (isLegacySecondHemistich(versesByVorder, vorder)) return null
        return distichPair(versesByVorder, vorder, secondPosition = 0L)
    }

    private fun distichPair(
        versesByVorder: Map<Long, List<VerseRow>>,
        vorder: Long,
        secondPosition: Long,
    ): List<String>? {
        val first = versesByVorder[vorder]?.find { it.position == 0L }?.text ?: return null
        val second =
            versesByVorder[vorder + 1]?.find { it.position == secondPosition }?.text
                ?: return null
        if (first.length > GameConstants.MAX_HEMISTICH_LENGTH ||
            second.length > GameConstants.MAX_HEMISTICH_LENGTH
        ) {
            return null
        }
        return listOf(first, second)
    }

    private fun sameVorderDistichLines(
        versesByVorder: Map<Long, List<VerseRow>>,
        vorder: Long,
    ): List<String>? {
        val rows = versesByVorder[vorder] ?: return null
        val first = rows.find { it.position == 0L }?.text ?: return null
        val second = rows.find { it.position == 1L }?.text ?: return null
        return if (first.length > GameConstants.MAX_HEMISTICH_LENGTH ||
            second.length > GameConstants.MAX_HEMISTICH_LENGTH
        ) {
            null
        } else {
            listOf(first, second)
        }
    }

    private fun ganjoorAlternatingDistichLines(
        versesByVorder: Map<Long, List<VerseRow>>,
        vorder: Long,
    ): List<String>? {
        if (isGanjoorAlternatingSecondHemistich(versesByVorder, vorder)) return null
        return distichPair(versesByVorder, vorder, secondPosition = 1L)
    }

    private fun isGanjoorAlternatingSecondHemistich(
        versesByVorder: Map<Long, List<VerseRow>>,
        vorder: Long,
    ): Boolean {
        val currentRows = versesByVorder[vorder] ?: return false
        val previousRows = versesByVorder[vorder - 1] ?: return false
        return currentRows.none { it.position == 0L } &&
            currentRows.any { it.position == 1L } &&
            previousRows.any { it.position == 0L }
    }

    private fun isLegacySecondHemistich(
        versesByVorder: Map<Long, List<VerseRow>>,
        vorder: Long,
    ): Boolean {
        if (vorder <= 0L) return false
        val previousRows = versesByVorder[vorder - 1] ?: return false
        val currentRows = versesByVorder[vorder] ?: return false
        return previousRows.any { it.position == 0L } &&
            previousRows.none { it.position == 1L } &&
            currentRows.any { it.position == 0L } &&
            currentRows.none { it.position == 1L }
    }
}
