package abkabk.azbarkon.app.core.di

import abkabk.azbarkon.app.core.network.AuthProvider
import abkabk.azbarkon.app.core.network.DefaultAuthProvider
import abkabk.azbarkon.app.core.network.createHttpClient
import abkabk.azbarkon.app.core.network.getEngine
import abkabk.azbarkon.app.data.remote.PoetApi
import abkabk.azbarkon.app.data.remote.PoetApiImpl
import io.ktor.client.engine.HttpClientEngine
import org.koin.dsl.module

val networkModule = module {

    single<HttpClientEngine> {
        getEngine()
    }

    single<AuthProvider> {
        DefaultAuthProvider()
    }

    single {
        createHttpClient(
            engine = get(),
            authProvider = get()
        )
    }

    single<PoetApi> {
        PoetApiImpl(get())
    }
}