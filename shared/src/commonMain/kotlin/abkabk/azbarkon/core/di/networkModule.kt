package abkabk.azbarkon.core.di

import abkabk.azbarkon.core.network.AuthProvider
import abkabk.azbarkon.core.network.DefaultAuthProvider
import abkabk.azbarkon.core.network.createHttpClient
import abkabk.azbarkon.core.network.getEngine
import abkabk.azbarkon.data.remote.PoetApi
import abkabk.azbarkon.data.remote.PoetApiImpl
import io.ktor.client.engine.HttpClientEngine
import org.koin.dsl.module

val networkModule =
    module {

        single<HttpClientEngine> {
            getEngine()
        }

        single<AuthProvider> {
            DefaultAuthProvider()
        }

        single {
            createHttpClient(
                engine = get(),
                authProvider = get(),
            )
        }

        single<PoetApi> {
            PoetApiImpl(get())
        }
    }
