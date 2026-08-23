package abkabk.azbarkon.core.util

import platform.Foundation.NSDate
import platform.Foundation.NSTimeZone
import platform.Foundation.localTimeZone

actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

actual fun localTimezoneOffsetMillis(): Long = (NSTimeZone.localTimeZone.secondsFromGMT * 1000).toLong()
