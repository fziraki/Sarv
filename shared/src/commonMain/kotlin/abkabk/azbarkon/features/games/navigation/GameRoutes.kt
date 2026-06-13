package abkabk.azbarkon.features.games.navigation

import abkabk.azbarkon.domain.model.games.GameType
import kotlinx.serialization.Serializable

@Serializable
data object GamesRoute

@Serializable
enum class GameTypeRoute {
    NEXT_VERSE,
    FIND_POET,
    COMPLETE_POEM,
    ORGANIZE_POEM,
}

@Serializable
data class GamePlayRoute(
    val type: GameTypeRoute,
)

@Serializable
data class GameResultRoute(
    val type: GameTypeRoute,
    val correct: Int,
    val wrong: Int,
    val noAnswer: Int,
    val scoreDelta: Int,
)

fun GameTypeRoute.toDomain(): GameType =
    when (this) {
        GameTypeRoute.NEXT_VERSE -> GameType.NEXT_VERSE
        GameTypeRoute.FIND_POET -> GameType.FIND_POET
        GameTypeRoute.COMPLETE_POEM -> GameType.COMPLETE_POEM
        GameTypeRoute.ORGANIZE_POEM -> GameType.ORGANIZE_POEM
    }

fun GameType.toRoute(): GameTypeRoute =
    when (this) {
        GameType.NEXT_VERSE -> GameTypeRoute.NEXT_VERSE
        GameType.FIND_POET -> GameTypeRoute.FIND_POET
        GameType.COMPLETE_POEM -> GameTypeRoute.COMPLETE_POEM
        GameType.ORGANIZE_POEM -> GameTypeRoute.ORGANIZE_POEM
    }
