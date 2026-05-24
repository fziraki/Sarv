package abkabk.azbarkon.app.core.network

import io.ktor.client.plugins.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.TimeoutCancellationException

suspend inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> T
): ApiResult<T> {

    return try {

        ApiResult.Success(apiCall())

    } catch (e: ClientRequestException) {

        ApiResult.Error(
            message = e.message ?: "Client error",
            code = e.response.status.value
        )

    } catch (e: ServerResponseException) {

        ApiResult.Error(
            message = e.message ?: "Server error",
            code = e.response.status.value
        )

    } catch (e: RedirectResponseException) {

        ApiResult.Error(
            message = e.message ?: "Redirect error",
            code = e.response.status.value
        )

    } catch (e: TimeoutCancellationException) {

        ApiResult.Error(
            message = "Request timeout"
        )

    } catch (e: IOException) {

        ApiResult.Error(
            message = "No internet connection"
        )

    } catch (e: Exception) {

        ApiResult.Error(
            message = e.message ?: "Unknown error"
        )
    }
}