package abkabk.azbarkon.app.core.di

import abkabk.azbarkon.app.core.network.DefaultAuthProvider
import abkabk.azbarkon.app.core.network.createHttpClient
import abkabk.azbarkon.app.core.network.getEngine
import io.ktor.client.HttpClient

fun provideHttpClient(): HttpClient {
    return createHttpClient(
        engine = getEngine(),
        authProvider = DefaultAuthProvider()
    )
}