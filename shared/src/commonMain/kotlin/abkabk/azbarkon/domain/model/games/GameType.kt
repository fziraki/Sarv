package abkabk.azbarkon.domain.model.games

enum class GameType {
    NEXT_VERSE,
    FIND_POET,
    COMPLETE_POEM,
    ORGANIZE_POEM,
}

fun GameType.baseScore(): Int =
    when (this) {
        GameType.NEXT_VERSE -> 10
        GameType.COMPLETE_POEM -> 15
        GameType.FIND_POET -> 20
        GameType.ORGANIZE_POEM -> 25
    }
