package abkabk.azbarkon.domain.model

data class UserInfo(
    val completedLevel: GameLevel? = null,
    val inProgressLevel: GameLevel? = null,
    val currentScore: Int? = null,
    val streakNumber: Int? = null,
    val poetsNumber: Int? = null,
    val poemsNumber: Int? = null,
    val badges: List<Badge> = emptyList()
)


data class GameLevel(
    val id: Int,
    val name: String,
    val totalScore: Int
)

data class Badge(
    val id: Int,
    val name: String
)