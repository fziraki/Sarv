package abkabk.azbarkon.domain.model.profile

import abkabk.azbarkon.domain.model.GameLevel

data class CatalogLevel(
    val id: Int,
    val name: String,
)

object GameLevelCatalog {
    const val XP_PER_LEVEL: Int = 900

    val levels: List<CatalogLevel> =
        listOf(
            CatalogLevel(1, "نوآموز واژه"),
            CatalogLevel(2, "رهگذر شعر"),
            CatalogLevel(3, "هم‌نشین غزل"),
            CatalogLevel(4, "حافظ ابیات"),
            CatalogLevel(5, "خواننده شب"),
            CatalogLevel(6, "یار شعر"),
            CatalogLevel(7, "استاد بیت"),
            CatalogLevel(8, "حکیم شعر"),
        )

    fun levelById(id: Int): CatalogLevel? = levels.firstOrNull { it.id == id }

    fun requiredScoreForLevel(levelId: Int): Int = (levelId - 1) * XP_PER_LEVEL

    fun progressFromCoinBalance(coinBalance: Int): ProfileLevelProgress {
        val cappedIndex = (coinBalance / XP_PER_LEVEL).coerceIn(0, levels.lastIndex)
        val currentLevel = levels[cappedIndex]
        return ProfileLevelProgress(
            levelId = currentLevel.id,
            levelName = currentLevel.name,
            currentXp = coinBalance % XP_PER_LEVEL,
            targetXp = XP_PER_LEVEL,
        )
    }

    fun levelRowState(
        levelId: Int,
        currentLevelId: Int,
    ): LevelRowState =
        when {
            levelId < currentLevelId -> LevelRowState.Completed
            levelId == currentLevelId -> LevelRowState.Current
            else -> LevelRowState.Locked
        }

    fun toGameLevel(catalogLevel: CatalogLevel): GameLevel =
        GameLevel(
            id = catalogLevel.id,
            name = catalogLevel.name,
            totalScore = XP_PER_LEVEL,
        )
}
