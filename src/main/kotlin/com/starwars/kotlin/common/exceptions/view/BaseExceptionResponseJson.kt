package com.starwars.kotlin.common.exceptions.view

open class BaseExceptionResponseJson(open val httpStatusCode: Int, open val message: String)