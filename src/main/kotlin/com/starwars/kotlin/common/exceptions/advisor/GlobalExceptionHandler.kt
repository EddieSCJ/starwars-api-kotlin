package com.starwars.kotlin.common.exceptions.advisor

import com.starwars.kotlin.common.exceptions.error.BadRequestError
import com.starwars.kotlin.common.exceptions.error.ConflictError
import com.starwars.kotlin.common.exceptions.error.NotFoundError
import com.starwars.kotlin.common.exceptions.error.UnauthorizedError
import com.starwars.kotlin.common.exceptions.view.BadRequestExceptionResponseJson
import com.starwars.kotlin.common.exceptions.view.BaseExceptionResponseJson
import com.starwars.kotlin.infra.helpers.MessageSourceHelper
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.server.MethodNotAllowedException
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class GlobalExceptionHandler @Autowired constructor(private val messageSourceHelper: MessageSourceHelper) {

    @ExceptionHandler(Exception::class)
    @ApiResponse(responseCode = "500", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = BaseExceptionResponseJson::class))])
    fun handleGenericException(ex: Exception): ResponseEntity<BaseExceptionResponseJson> {
        val response = BaseExceptionResponseJson(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.message!!)
        return ResponseEntity.status(response.httpStatusCode).body(response)
    }

    @ExceptionHandler(BadRequestError::class)
    @ApiResponse(responseCode = "400", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = BadRequestExceptionResponseJson::class))])
    fun handleBadRequest(exception: BadRequestError): ResponseEntity<BadRequestExceptionResponseJson> {
        val response = BadRequestExceptionResponseJson(HttpStatus.BAD_REQUEST.value(), messageSourceHelper.getApiErrorMessage("bad_request"), exception.errors)
        return ResponseEntity.status(response.httpStatusCode).body(response)
    }

    @ExceptionHandler(UnauthorizedError::class)
    @ApiResponse(responseCode = "401", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = BaseExceptionResponseJson::class))])
    fun handleUnauthorizedException(ex: Exception): ResponseEntity<BaseExceptionResponseJson> {
        val response = BaseExceptionResponseJson(HttpStatus.UNAUTHORIZED.value(), ex.message!!)
        return ResponseEntity.status(response.httpStatusCode).body(response)
    }

    @ExceptionHandler(NotFoundError::class, WebClientResponseException.NotFound::class, HttpClientErrorException.NotFound::class)
    @ApiResponse(responseCode = "404", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = BaseExceptionResponseJson::class))])
    fun handleNotFoundException(ex: Exception): ResponseEntity<BaseExceptionResponseJson> {
        val response = BaseExceptionResponseJson(NotFoundError.HTTP_STATUS_CODE, ex.message!!)
        return ResponseEntity.status(response.httpStatusCode).body(response)
    }



    @ExceptionHandler(ConflictError::class)
    @ApiResponse(responseCode = "409", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = BaseExceptionResponseJson::class))])
    fun handleConflictException(ex: ConflictError): ResponseEntity<BaseExceptionResponseJson> {
        val response = BaseExceptionResponseJson(ConflictError.HTTP_STATUS_CODE, ex.message!!)
        return ResponseEntity.status(response.httpStatusCode).body(response)
    }
}