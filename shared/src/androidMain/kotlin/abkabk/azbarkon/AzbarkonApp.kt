package abkabk.azbarkon

import abkabk.azbarkon.core.di.androidModule
import abkabk.azbarkon.core.di.dataModule
import abkabk.azbarkon.core.di.databaseModule
import abkabk.azbarkon.core.di.domainModule
import abkabk.azbarkon.core.di.networkModule
import abkabk.azbarkon.core.di.presentationModule
import android.app.Application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AzbarkonApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AzbarkonApp)
            modules(
                networkModule,
                databaseModule,
                dataModule,
                domainModule,
                presentationModule,
                androidModule,
            )
        }
        Napier.base(DebugAntilog())
    }
}
