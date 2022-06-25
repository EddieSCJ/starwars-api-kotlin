package com.starwars.kotlin.infra.helpers

import org.springframework.context.MessageSource
import java.text.MessageFormat
import java.util.*

class MessageSourceHelper(private val messageSource: MessageSource) {
    fun getApiErrorMessage(error: String): String =
        messageSource.getMessage(MessageFormat.format("error.api.{0}", error), null, Locale("pt-br"))

    fun getFieldErrorMessage(error: String): String =
        messageSource.getMessage(MessageFormat.format("field.error.{0}", error), null, Locale("pt-br"))
}