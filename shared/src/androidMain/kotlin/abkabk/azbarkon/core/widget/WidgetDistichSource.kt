package abkabk.azbarkon.core.widget

import abkabk.azbarkon.core.util.currentLocalDateSeed

enum class WidgetDistichSource {
    Daily,
    Random,
}

fun dailyDistichSeed(poetId: Int): Long =
    if (poetId == RandomDistichWidgetConstants.ALL_POETS_ID) {
        currentLocalDateSeed()
    } else {
        currentLocalDateSeed() * POET_SEED_SPACE + poetId
    }

private const val POET_SEED_SPACE = 1_000_000L

fun randomDistichSeed(appWidgetId: Int): Long = System.currentTimeMillis() + appWidgetId
