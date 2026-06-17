package abkabk.azbarkon.core.network

import io.ktor.client.plugins.api.createClientPlugin

class AuthPluginConfig {
    lateinit var provider: AuthProvider
}

val AuthPlugin =
    createClientPlugin(
        name = "AuthPlugin",
        createConfiguration = ::AuthPluginConfig,
    ) {

        val provider = pluginConfig.provider

        onRequest { request, _ ->

            val isAuthorizable =
                request.headers["isAuthorizable"]?.toBoolean() ?: true

            request.headers.remove("isAuthorizable")

            if (isAuthorizable) {
                provider.getToken()?.let { token ->
                    request.headers.append(
                        "Authorization",
                        "Bearer $token",
                    )
                }
            }
        }
    }
