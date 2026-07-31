package abkabk.azbarkon.domain.model.memorization

enum class SrsGrade {
    AGAIN,
    HARD,
    GOOD,
    EASY,
}

enum class ActiveMemorizationStatus {
    ACTIVE,
    PAUSED,
}

data class SrsCard(
    val id: Long,
    val poemId: Int,
    val cardIndex: Int,
    val front: String,
    val back: String,
    val interval: Int,
    val ease: Double,
    val dueDateMillis: Long,
    val consecutiveCorrect: Int,
)

data class MemorizationSummary(
    val activePoemCount: Int,
    val dueCardsToday: Int,
)

data class ActiveMemorizationPoem(
    val poemId: Int,
    val title: String,
    val poetName: String,
    val categoryName: String,
    val addedAtMillis: Long,
    val status: ActiveMemorizationStatus,
    val totalCards: Int,
    val reviewedCards: Int,
    val dueCards: Int,
    val boxLevel: Int,
    val level: Int,
)

data class QuickStartTarget(
    val poetId: Int? = null,
    val catId: Int? = null,
    val catTitle: String? = null,
)

sealed interface MemorizationError : abkabk.azbarkon.core.domain.result.Error {
    data object MaxActivePoemsReached : MemorizationError

    data object PoemNotFound : MemorizationError

    data object CardNotFound : MemorizationError

    data object Unknown : MemorizationError
}
