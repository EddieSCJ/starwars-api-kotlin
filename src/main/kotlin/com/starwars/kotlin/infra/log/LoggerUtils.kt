package com.starwars.kotlin.infra.log

import com.starwars.kotlin.planets.app.handler.OperationsEnum
import org.apache.commons.lang3.StringUtils
import org.slf4j.MDC
import org.springframework.http.server.reactive.ServerHttpRequest
import java.util.*

object LoggerUtils {
    private const val OPERATION = "operation"
    private const val OPERATION_KEY = "operation_key"
    private const val HTTP_METHOD = "http_method"
    private const val PATH = "path"

    fun setOperationInfoIntoMDC(resourceKey: String?, operation: OperationsEnum, request: ServerHttpRequest) {
        setOperationInfoIntoMDC(operation, request)
        MDC.put(OPERATION_KEY, resourceKey)
    }

    fun setOperationInfoIntoMDC(operation: OperationsEnum, request: ServerHttpRequest) {
        setTID(request)
        MDC.put(OPERATION, operation.operationName)
        MDC.put(HTTP_METHOD, operation.method.name)
        MDC.put(PATH, operation.path)
    }

    fun setTID(request: ServerHttpRequest) {
        var tid: String? = request.headers.getFirst("tid")
        if (StringUtils.isNotEmpty(tid)) {
            MDC.put("tid", tid)
            return
        }

        val prefix = System.getProperty("TID_PREFIX")
        tid = if (prefix != null) prefix + "-" + UUID.randomUUID() else UUID.randomUUID().toString()
        MDC.put("tid", tid)
    }
}