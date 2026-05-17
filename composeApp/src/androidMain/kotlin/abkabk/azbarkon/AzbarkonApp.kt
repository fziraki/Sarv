package abkabk.azbarkon

import abkabk.azbarkon.app.core.di.KoinInit
import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class AzbarkonApp : Application() {

    override fun onCreate() {
        super.onCreate()

        KoinInit.init()
        Napier.base(DebugAntilog())
    }
}