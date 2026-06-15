package abkabk.azbarkon.domain.model.profile

import abkabk.azbarkon.domain.model.GameLevel

data class MemorizationProfileStats(
    val practiceStreak: Int = 0,
    val memorizingPoetsCount: Int = 0,
    val inProgressPoemCount: Int = 0,
)

data class GameProfileStats(
    val visitStreak: Int = 0,
    val totalCorrectAnswers: Int = 0,
    val totalWrongAnswers: Int = 0,
    val coinBalance: Int = 0,
    val completedSessions: Int = 0,
    val perfectGameSessions: Int = 0,
)

data class LevelRequirement(
    val label: String,
    val current: Int,
    val target: Int,
) {
    val isComplete: Boolean
        get() = current >= target
}

data class GameLevelDetail(
    val level: GameLevel,
    val description: String,
    val currentXp: Int,
    val targetXp: Int,
    val upgradeRequirements: List<LevelRequirement>,
)

enum class ProfileSheet {
    Settings,
    Badges,
    Levels,
    LevelDetail,
}

enum class LevelRowState {
    Completed,
    Current,
    Locked,
}

data class LevelListItemUi(
    val level: GameLevel,
    val description: String,
    val state: LevelRowState,
)

data class BadgeUi(
    val id: Int,
    val name: String,
    val description: String,
    val isEarned: Boolean,
)

data class ProfileLevelProgress(
    val levelId: Int,
    val levelName: String,
    val currentXp: Int,
    val targetXp: Int,
)
