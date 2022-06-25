package com.starwars.kotlin.common.exceptions.error

import java.lang.RuntimeException
import org.springframework.http.HttpStatus

data class ConflictError(override val message: String) : RuntimeException(message) {
    companion object {
        val HTTP_STATUS_CODE = HttpStatus.CONFLICT.value()
    }
}