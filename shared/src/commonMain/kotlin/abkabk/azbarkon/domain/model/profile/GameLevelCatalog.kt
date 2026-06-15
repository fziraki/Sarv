package abkabk.azbarkon.domain.model.profile

import abkabk.azbarkon.domain.model.GameLevel

data class CatalogLevel(
    val id: Int,
    val name: String,
    val description: String,
)

object GameLevelCatalog {
    const val XP_PER_LEVEL: Int = 900

    val levels: List<CatalogLevel> =
        listOf(
            CatalogLevel(1, "نوآموز واژه", "آشنایی با واژه‌های شعر"),
            CatalogLevel(2, "رهگذر شعر", "گذر از میان ابیات"),
            CatalogLevel(3, "هم‌نشین غزل", "همراهی با غزل‌های زیبا"),
            CatalogLevel(4, "حافظ ابیات", "حفظ و مرور منظم شعر"),
            CatalogLevel(5, "خواننده شب", "شب‌های پیاپی با شعر"),
            CatalogLevel(6, "یار شعر", "پیوند عمیق با شاعران"),
            CatalogLevel(7, "استاد بیت", "تسلط بر ابیات"),
            CatalogLevel(8, "حکیم شعر", "درک عمیق از شعر فارسی"),
        )

    fun levelById(id: Int): CatalogLevel? = levels.firstOrNull { it.id == id }

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

    fun upgradeRequirements(
        levelId: Int,
        memorizingPoetsCount: Int,
        reviewedVersesCount: Int,
        gameVisitStreak: Int,
    ): List<LevelRequirement> {
        if (levelId >= levels.size) return emptyList()

        return listOf(
            LevelRequirement(
                label = "۷ شاعر مختلف بخوانید",
                current = memorizingPoetsCount.coerceAtMost(7),
                target = 7,
            ),
            LevelRequirement(
                label = "۵۰۰ بیت حفظ کرده باشید",
                current = reviewedVersesCount.coerceAtMost(500),
                target = 500,
            ),
            LevelRequirement(
                label = "۳ روز پیاپی بازی کنید",
                current = gameVisitStreak.coerceAtMost(3),
                target = 3,
            ),
        )
    }
}
