package abkabk.azbarkon.domain.model.games

private const val NEXT_VERSE_BASE_SCORE = 10
private const val FIND_POET_BASE_SCORE = 20
private const val COMPLETE_POEM_BASE_SCORE = 15
private const val ORGANIZE_POEM_BASE_SCORE = 25

enum class GameType(val baseScore: Int) {
    NEXT_VERSE(NEXT_VERSE_BASE_SCORE),
    FIND_POET(FIND_POET_BASE_SCORE),
    COMPLETE_POEM(COMPLETE_POEM_BASE_SCORE),
    ORGANIZE_POEM(ORGANIZE_POEM_BASE_SCORE),
}
