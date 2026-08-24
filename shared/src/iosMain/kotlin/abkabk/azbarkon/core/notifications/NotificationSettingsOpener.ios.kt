package abkabk.azbarkon.core.notifications

import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.Foundation.NSURL

actual fun openAppNotificationSettings() {
    val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString) ?: return
    UIApplication.sharedApplication.openURL(url)
}