package abkabk.azbarkon.core.di

import abkabk.azbarkon.core.network.AuthProvider
import abkabk.azbarkon.core.network.DefaultAuthProvider
import abkabk.azbarkon.core.network.HttpClientFactory
import abkabk.azbarkon.core.network.getEngine
import io.ktor.client.HttpClient
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

        single<HttpClient> {
            HttpClientFactory.create(
                engine = get(),
                authProvider = get(),
            )
        }
    }
