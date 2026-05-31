package abkabk.azbarkon

import abkabk.azbarkon.core.di.initKoin
import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class AzbarkonApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(this)
        Napier.base(DebugAntilog())
    }
}
