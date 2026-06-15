package abkabk.azbarkon.domain.model.games

data class GameDistichCandidate(
    val poemId: Long,
    val vorder: Long,
    val firstHemistich: String,
    val secondHemistich: String,
    val poetId: Long,
    val poetName: String,
)

data class GameOrganizeWindow(
    val poemId: Int,
    val startVorder: Int,
    val lines: List<String>,
)

data class GamePoemBundle(
    val poemId: Long,
    val poetId: Long,
    val poetName: String,
    val distichs: List<GameDistichCandidate>,
    val organizeWindows: List<GameOrganizeWindow>,
    val poemWords: List<String>,
)
