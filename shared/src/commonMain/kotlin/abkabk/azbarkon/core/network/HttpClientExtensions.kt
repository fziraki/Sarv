package abkabk.azbarkon.core.network

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.util.Constants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

suspend inline fun <reified Response : Any> HttpClient.getResult(
    route: String,
    queryParameters: Map<String, Any?> = emptyMap(),
): Result<Response, DataError.Network> =
    safeCall {
        get(constructRoute(route)) {
            queryParameters.forEach { (key, value) ->
                if (value != null) {
                    url.parameters.append(key, value.toString())
                }
            }
        }
    }

suspend inline fun <reified Request, reified Response : Any> HttpClient.postResult(
    route: String,
    body: Request,
): Result<Response, DataError.Network> =
    safeCall {
        post(constructRoute(route)) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

suspend inline fun <reified Response : Any> HttpClient.deleteResult(
    route: String,
    queryParameters: Map<String, Any?> = emptyMap(),
): Result<Response, DataError.Network> =
    safeCall {
        delete(constructRoute(route)) {
            queryParameters.forEach { (key, value) ->
                if (value != null) {
                    url.parameters.append(key, value.toString())
                }
            }
        }
    }

suspend inline fun <reified T> safeCall(execute: suspend () -> HttpResponse): Result<T, DataError.Network> =
    runCatching { execute() }.fold(
        onSuccess = { responseToResult(it) },
        onFailure = { e ->
            if (e is TimeoutCancellationException) return@fold Result.Error(DataError.Network.REQUEST_TIMEOUT)
            if (e is CancellationException) throw e
            when (e) {
                is SerializationException -> Result.Error(DataError.Network.SERIALIZATION)
                is IOException -> Result.Error(DataError.Network.NO_INTERNET)
                is ClientRequestException -> mapHttpStatus(e.response.status)
                is ServerResponseException -> mapHttpStatus(e.response.status)
                else -> Result.Error(DataError.Network.UNKNOWN)
            }
        },
    )

suspend inline fun <reified T> responseToResult(response: HttpResponse): Result<T, DataError.Network> =
    when {
        response.status.isSuccess() ->
            runCatching { response.body<T>() }.fold(
                onSuccess = { Result.Success(it) },
                onFailure = { Result.Error(DataError.Network.SERIALIZATION) },
            )

        response.status == HttpStatusCode.BadRequest -> Result.Error(DataError.Network.BAD_REQUEST)
        response.status == HttpStatusCode.Unauthorized -> Result.Error(DataError.Network.UNAUTHORIZED)
        response.status == HttpStatusCode.Forbidden -> Result.Error(DataError.Network.FORBIDDEN)
        response.status == HttpStatusCode.NotFound -> Result.Error(DataError.Network.NOT_FOUND)
        response.status == HttpStatusCode.RequestTimeout -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
        response.status == HttpStatusCode.Conflict -> Result.Error(DataError.Network.CONFLICT)
        response.status == HttpStatusCode.PayloadTooLarge -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        response.status == HttpStatusCode.TooManyRequests -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
        response.status.isServerError() -> Result.Error(DataError.Network.SERVER_ERROR)
        response.status == HttpStatusCode.ServiceUnavailable -> Result.Error(DataError.Network.SERVICE_UNAVAILABLE)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }

fun <T> mapHttpStatus(statusCode: HttpStatusCode): Result<T, DataError.Network> =
    when {
        statusCode == HttpStatusCode.BadRequest -> Result.Error(DataError.Network.BAD_REQUEST)
        statusCode == HttpStatusCode.Unauthorized -> Result.Error(DataError.Network.UNAUTHORIZED)
        statusCode == HttpStatusCode.Forbidden -> Result.Error(DataError.Network.FORBIDDEN)
        statusCode == HttpStatusCode.NotFound -> Result.Error(DataError.Network.NOT_FOUND)
        statusCode == HttpStatusCode.RequestTimeout -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
        statusCode == HttpStatusCode.Conflict -> Result.Error(DataError.Network.CONFLICT)
        statusCode == HttpStatusCode.PayloadTooLarge -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        statusCode == HttpStatusCode.TooManyRequests -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
        statusCode.isServerError() -> Result.Error(DataError.Network.SERVER_ERROR)
        statusCode == HttpStatusCode.ServiceUnavailable -> Result.Error(DataError.Network.SERVICE_UNAVAILABLE)
        else -> Result.Error(DataError.Network.UNKNOWN)
    }

private const val SUCCESS_MIN_CODE = 200
private const val SUCCESS_MAX_CODE = 299
private const val SERVER_ERROR_MAX_CODE = 599

fun HttpStatusCode.isSuccess(): Boolean =
    value in SUCCESS_MIN_CODE..SUCCESS_MAX_CODE

fun HttpStatusCode.isServerError(): Boolean =
    value in HttpStatusCode.InternalServerError.value..SERVER_ERROR_MAX_CODE

fun constructRoute(route: String): String =
    when {
        route.startsWith("http") -> route
        route.startsWith("/") -> Constants.BASE_URL.trimEnd('/') + route
        else -> "${Constants.BASE_URL.trimEnd('/')}/$route"
    }
