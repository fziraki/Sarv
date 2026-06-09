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
        currentLocalDateSeed() * 1_000_000L + poetId
    }

fun randomDistichSeed(appWidgetId: Int): Long = System.currentTimeMillis() + appWidgetId
