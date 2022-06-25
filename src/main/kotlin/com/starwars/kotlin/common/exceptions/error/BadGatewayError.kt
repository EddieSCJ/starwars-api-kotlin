package com.starwars.kotlin.common.exceptions.error

import org.springframework.http.HttpStatus

data class BadGatewayError(private val errors: List<String>) : RuntimeException() {
    companion object {
        val HTTP_STATUS_CODE: Int = HttpStatus.BAD_GATEWAY.value()
    }
}