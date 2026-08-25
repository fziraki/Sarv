package abkabk.azbarkon.features.profile.util

import platform.Foundation.NSBundle

actual fun versionName(): String =
    NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String ?: ""
