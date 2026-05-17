package abkabk.azbarkon.app.core.di

import org.koin.core.context.startKoin

object KoinInit {
    fun init() {
        startKoin {
            modules(appModules)
        }
    }
}