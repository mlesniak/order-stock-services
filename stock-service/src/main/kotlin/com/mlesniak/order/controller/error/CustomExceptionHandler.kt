package com.mlesniak.order.controller.error

import com.mlesniak.order.controller.model.RestResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * Default error handler for exceptions we have not caught.
 */
@ControllerAdvice
class CustomExceptionHandler {
    // REMARK In a future version one could have multiple handlers and return different
    //        status codes (e.g. differentiate between bad requests and server errors).
    @ExceptionHandler
    fun handleExceptions(exception: Exception): ResponseEntity<RestResult<Any>>? {
        return ResponseEntity
            .badRequest()
            .body(RestResult(exception.message ?: "Unknown error", null, HttpStatus.BAD_REQUEST))
    }
}