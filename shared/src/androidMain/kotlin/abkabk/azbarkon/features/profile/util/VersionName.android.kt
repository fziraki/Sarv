package abkabk.azbarkon.features.profile.util

import android.content.Context
import org.koin.core.context.GlobalContext

actual fun versionName(): String {
    val context = GlobalContext.get().get<Context>()
    return context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
}
