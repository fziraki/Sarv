package abkabk.azbarkon.domain.model.profile

data class CatalogBadge(
    val id: Int,
    val name: String,
    val description: String,
)

object BadgeCatalog {
    val badges: List<CatalogBadge> =
        listOf(
            CatalogBadge(
                id = 1,
                name = "اولین غزل",
                description = "یک غزل را کامل حفظ کنید",
            ),
            CatalogBadge(
                id = 2,
                name = "صد بیت",
                description = "۱۰۰ بیت حفظ کنید",
            ),
            CatalogBadge(
                id = 3,
                name = "هفت شب پیاپی",
                description = "۷ روز پیاپی بازی کنید",
            ),
            CatalogBadge(
                id = 4,
                name = "علاقه‌مند شعر",
                description = "۱۰ شاعر مختلف بخوانید",
            ),
            CatalogBadge(
                id = 5,
                name = "ستاره‌ی حافظ",
                description = "۵ بازی را با امتیاز کامل ببرید",
            ),
        )

    fun resolveEarned(
        badgeId: Int,
        hasCompletedMemorizationPoem: Boolean,
        reviewedVersesCount: Int,
        gameVisitStreak: Int,
        perfectGameSessions: Int,
    ): Boolean =
        when (badgeId) {
            1 -> hasCompletedMemorizationPoem
            2 -> reviewedVersesCount >= 100
            3 -> gameVisitStreak >= 7
            4 -> false
            5 -> perfectGameSessions >= 5
            else -> false
        }

    fun toBadgeUi(
        badge: CatalogBadge,
        hasCompletedMemorizationPoem: Boolean,
        reviewedVersesCount: Int,
        gameVisitStreak: Int,
        perfectGameSessions: Int,
    ): BadgeUi =
        BadgeUi(
            id = badge.id,
            name = badge.name,
            description = badge.description,
            isEarned =
                resolveEarned(
                    badgeId = badge.id,
                    hasCompletedMemorizationPoem = hasCompletedMemorizationPoem,
                    reviewedVersesCount = reviewedVersesCount,
                    gameVisitStreak = gameVisitStreak,
                    perfectGameSessions = perfectGameSessions,
                ),
        )
}
