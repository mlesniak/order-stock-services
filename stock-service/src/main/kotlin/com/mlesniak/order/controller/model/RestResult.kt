package com.mlesniak.order.controller.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

typealias ResponseResult<T> = ResponseEntity<RestResult<T>>

/**
 * This is the basic result class for all REST calls.
 *
 * It allows to have a consistent structure for all REST calls,
 * successfully or erroneous ones. Its structure is loosely based
 * on the (informal) JSend specification, see
 * https://github.com/omniti-labs/jsend, though we omit [status]
 * since it is described by the HTTP status code.
 *
 * For a successful call, the [message] is null and the [data] is set.
 * For an error, the [message] is set, [data] is null and the status
 * code allows the client to distinguish between different types of
 * errors.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class RestResult<T>(
    val message: String? = null,
    val data: T? = null,

    @JsonIgnore
    val statusCode: HttpStatus,
) {
    fun build(): ResponseEntity<RestResult<T>> {
        if (data == null && message == null) {
            return ResponseEntity.status(statusCode).build()
        }

        return ResponseEntity.status(statusCode).body(this)
    }

    // Convenience methods with appropriate default values.
    companion object {
        fun <T> ok(data: T? = null, statusCode: HttpStatus = HttpStatus.OK): ResponseEntity<RestResult<T>> =
            RestResult(null, data, statusCode).build()

        fun <T> badRequest(message: String, data: T? = null, statusCode: HttpStatus = HttpStatus.BAD_REQUEST): ResponseEntity<RestResult<T>> =
            RestResult(message, data, statusCode).build()

        fun <T> error(message: String, data: T? = null): ResponseEntity<RestResult<T>> =
            RestResult(message, data, HttpStatus.INTERNAL_SERVER_ERROR).build()
    }
}