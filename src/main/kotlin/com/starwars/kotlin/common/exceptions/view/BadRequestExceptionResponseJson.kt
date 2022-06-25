package com.starwars.kotlin.common.exceptions.view

data class BadRequestExceptionResponseJson(override val httpStatusCode: Int, override val message: String, val errors: List<String>) :
    BaseExceptionResponseJson(httpStatusCode, message)