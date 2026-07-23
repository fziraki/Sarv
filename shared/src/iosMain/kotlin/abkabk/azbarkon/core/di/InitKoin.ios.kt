package abkabk.azbarkon.core.di

import org.koin.core.context.startKoin

private var isKoinInitialized = false

fun initKoinIfNeeded() {
    if (isKoinInitialized) return

    startKoin {
        modules(sharedModules + iosPlatformModule + playerPlatformModule)
    }
    isKoinInitialized = true
}
