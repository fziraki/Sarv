package abkabk.azbarkon.core.util

import java.util.Calendar

actual fun currentLocalDateSeed(): Long {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.YEAR) * 10_000L +
        (calendar.get(Calendar.MONTH) + 1) * 100L +
        calendar.get(Calendar.DAY_OF_MONTH)
}
