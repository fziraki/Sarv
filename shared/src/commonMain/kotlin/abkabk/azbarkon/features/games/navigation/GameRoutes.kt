package abkabk.azbarkon.features.games.navigation

import abkabk.azbarkon.domain.model.games.GameType
import kotlinx.serialization.Serializable

@Serializable
data object GamesRoute

enum class GameTypeRoute {
    NEXT_VERSE,
    FIND_POET,
    COMPLETE_POEM,
    ORGANIZE_POEM,
}

@Serializable
data class GamePlayRoute(
    val type: String,
)

@Serializable
data class GameResultRoute(
    val type: String,
    val correct: Int,
    val wrong: Int,
    val noAnswer: Int,
    val scoreDelta: Int,
)

fun GamePlayRoute(type: GameTypeRoute): GamePlayRoute = GamePlayRoute(type.name)

fun GameResultRoute(
    type: GameTypeRoute,
    correct: Int,
    wrong: Int,
    noAnswer: Int,
    scoreDelta: Int,
): GameResultRoute =
    GameResultRoute(
        type = type.name,
        correct = correct,
        wrong = wrong,
        noAnswer = noAnswer,
        scoreDelta = scoreDelta,
    )

fun GamePlayRoute.toGameTypeRoute(): GameTypeRoute = GameTypeRoute.valueOf(type)

fun GameResultRoute.toGameTypeRoute(): GameTypeRoute = GameTypeRoute.valueOf(type)

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
