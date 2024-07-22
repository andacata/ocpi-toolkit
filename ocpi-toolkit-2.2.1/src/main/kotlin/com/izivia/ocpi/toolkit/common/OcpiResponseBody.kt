package com.izivia.ocpi.toolkit.common

import com.fasterxml.jackson.core.JsonProcessingException
import com.izivia.ocpi.toolkit.common.context.currentResponseMessageRoutingHeadersOrNull
import com.izivia.ocpi.toolkit.common.validation.toReadableString
import com.izivia.ocpi.toolkit.transport.domain.*
import org.apache.logging.log4j.LogManager
import org.valiktor.ConstraintViolationException
import java.time.Instant

/**
 * When the status code is in the success range (1xxx), the data field in the response message should contain the
 * information as specified in the protocol. Otherwise the data field is unspecified and may be omitted, null or
 * something else that could help to debug the problem from a programmer's perspective. For example, it could specify
 * which fields contain an error or are missing.
 *
 * The content that is sent with all the response messages is an 'application/json' type and contains a JSON object with
 * the following properties:
 *
 * @property data Contains the actual response data object or list of objects from each request, depending on the
 * cardinality of the response data, this is an array (card. * or +), or a single object (card. 1 or ?)
 * @property statusCode Response code, as listed in Status Codes, indicates how the request was handled. To avoid
 * confusion with HTTP codes, at least four digits are used.
 * @property statusMessage An optional status message which may help when debugging.
 * @property timestamp The time this message was generated.
 */
data class OcpiResponseBody<T>(
    val data: T?,
    val statusCode: Int,
    val statusMessage: String?,
    val timestamp: Instant
) {
    companion object {
        // updating this function is done only for tests
        // sadly, this is not thread safe, so tests cant be run in parallel
        // but reviewing the API at this time would be too cumbersome
        var now: () -> Instant = { Instant.now() }

        fun <T> success(data: T) = OcpiResponseBody(
            data = data,
            statusCode = OcpiStatus.SUCCESS.code,
            statusMessage = "Success",
            timestamp = now()
        )

        fun <T> invalid(message: String) = OcpiResponseBody<T>(
            data = null,
            statusCode = OcpiStatus.CLIENT_INVALID_PARAMETERS.code,
            statusMessage = message,
            timestamp = now()
        )

        suspend fun <T> of(data: suspend () -> T) =
            try {
                success(data = data())
            } catch (e: ConstraintViolationException) {
                invalid(message = e.toReadableString())
            }
    }
}

private val logger = LogManager.getLogger(OcpiResponseBody::class.java)

/**
 * Generates all required headers for a paginated response from a OcpiBody with a searchResult.
 * @param request the request that generated the body.
 * @return Map<String, String> the headers required for pagination
 */
fun OcpiResponseBody<SearchResult<*>>.getPaginatedHeaders(request: HttpRequest): Map<String, String> =
    if (data != null) {
        val nextPageOffset = (data.offset + data.limit).takeIf { it <= data.totalCount }

        val queries = request
            .queryParams
            .filter { it.key != "offset" && it.value != null }
            .plus("offset" to (data.limit + data.offset))
            .map { "${it.key}=${it.value}" }
            .joinToString("&", "?")

        listOfNotNull(
            nextPageOffset?.let { "Link" to "<${request.baseUrl}${request.path}$queries>; rel=\"next\"" },
            "X-Total-Count" to data.totalCount.toString(),
            "X-Limit" to data.limit.toString()
        ).toMap()
    } else {
        emptyMap()
    }

/**
 * Transforms an OcpiException to an HttpResponse. May be used in TransportServer implementation to handle
 * OCPI exceptions.
 */
fun OcpiException.toHttpResponse(): HttpResponse =
    HttpResponse(
        status = httpStatus,
        body = mapper.writeValueAsString(
            OcpiResponseBody(
                data = null,
                statusCode = ocpiStatus.code,
                statusMessage = message,
                timestamp = Instant.now()
            )
        ),
        headers = if (httpStatus == HttpStatus.UNAUTHORIZED) mapOf("WWW-Authenticate" to "Token") else emptyMap()
    )

/**
 * Used to handle errors & paginated responses when handling a request. fn() should contain the code generating the
 * body. If an error is caught, everything will be handled here. If it's a paginated response, it will be automatically
 * be handled too.
 *
 * @return the HttpResponse properly formatted according to the body generated by fn()
 */
@Suppress("UNCHECKED_CAST")
suspend fun <T> HttpRequest.httpResponse(fn: suspend () -> OcpiResponseBody<T>): HttpResponse {
    var baseHeaders = mapOf(Header.CONTENT_TYPE to ContentType.APPLICATION_JSON)

    return try {
        baseHeaders = baseHeaders
            .plus(getDebugHeaders())
            .plus(currentResponseMessageRoutingHeadersOrNull()?.httpHeaders().orEmpty())

        val ocpiResponseBody = fn()
        val isPaginated = ocpiResponseBody.data is SearchResult<*>

        HttpResponse(
            status = when (ocpiResponseBody.statusCode) {
                OcpiStatus.SUCCESS.code ->
                    if (this.method == HttpMethod.DELETE || ocpiResponseBody.data != null) {
                        HttpStatus.OK
                    } else {
                        HttpStatus.NOT_FOUND
                    }

                OcpiStatus.CLIENT_INVALID_PARAMETERS.code -> HttpStatus.BAD_REQUEST
                else -> HttpStatus.OK
            },
            body = mapper.writeValueAsString(
                if (isPaginated) {
                    OcpiResponseBody(
                        data = (ocpiResponseBody.data as SearchResult<*>?)?.list,
                        statusCode = ocpiResponseBody.statusCode,
                        statusMessage = ocpiResponseBody.statusMessage,
                        timestamp = ocpiResponseBody.timestamp
                    )
                } else {
                    ocpiResponseBody
                }
            ),
            headers = baseHeaders
        ).let {
            if (isPaginated) {
                it.copy(
                    headers = it.headers + (ocpiResponseBody as OcpiResponseBody<SearchResult<*>>)
                        .getPaginatedHeaders(request = this)
                )
            } else {
                it
            }
        }
    } catch (e: OcpiException) {
        e.toHttpResponse()
            .let { it.copy(headers = baseHeaders.plus(it.headers)) }
    } catch (e: HttpException) {
        logger.error(e)
        HttpResponse(
            status = e.status,
            headers = baseHeaders
        )
    } catch (e: JsonProcessingException) {
        logger.error(e)
        HttpResponse(
            status = HttpStatus.BAD_REQUEST,
            headers = baseHeaders
        )
    }
}
