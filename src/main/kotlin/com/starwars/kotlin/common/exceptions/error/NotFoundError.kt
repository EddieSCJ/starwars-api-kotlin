package com.starwars.kotlin.common.exceptions.error

import org.springframework.http.HttpStatus

data class NotFoundError(override val message: String) : RuntimeException(message) {
    companion object {
        val HTTP_STATUS_CODE: Int = HttpStatus.NOT_FOUND.value()
    }
}