package abkabk.azbarkon.core.notifications

import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.Foundation.NSURL

actual fun openAppNotificationSettings() {
    UIApplication.sharedApplication.openURL(NSURL.URLWithString(UIApplicationOpenSettingsURLString))
}