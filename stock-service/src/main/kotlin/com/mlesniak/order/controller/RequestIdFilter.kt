package com.mlesniak.order.controller

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.UUID

/**
 * Filter to add a request id to the response header 'X-Request-Id' (and MDC),
 * if none already exists. We use a random UUID if none is provided.
 */
@Component
class RequestIdFilter : OncePerRequestFilter() {
    private val headerName = "X-Request-Id"
    private val mdcKey = "requestId"

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        try {
            val token = request.getHeader(headerName) ?: UUID.randomUUID().toString()
            MDC.put(mdcKey, token)
            response.addHeader(headerName, token)
            chain.doFilter(request, response)
        } finally {
            MDC.remove(mdcKey)
        }
    }
}

