package abkabk.azbarkon.core.util

import java.util.TimeZone

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

actual fun localTimezoneOffsetMillis(): Long = TimeZone.getDefault().getOffset(System.currentTimeMillis()).toLong()
