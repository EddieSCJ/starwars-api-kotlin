package com.starwars.kotlin.common.exceptions.error

import org.springframework.http.HttpStatus

data class InternalServerError(override val message: String) : RuntimeException(message) {
    companion object {
        val HTTP_STATUS_CODE: Int = HttpStatus.INTERNAL_SERVER_ERROR.value()
    }
}