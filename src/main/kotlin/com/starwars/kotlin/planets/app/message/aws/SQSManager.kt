package com.starwars.kotlin.planets.app.message.aws

import com.starwars.kotlin.planets.domain.message.MessageSender
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Service
import java.text.MessageFormat
import java.util.*

@Service
class SQSManager constructor(
    private val queueMessagingTemplate: QueueMessagingTemplate,
    @Value("\${cloud.aws.sqs.planet-delete-uri}") private val planetDeleteQueueURL: String
) : MessageSender {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun sendMessage(message: String) {
        val headers: MutableMap<String, Any> = HashMap()
        headers["message-group-id"] = UUID.randomUUID().toString()
        queueMessagingTemplate.convertAndSend<String>(planetDeleteQueueURL, message, headers)
        log.info(MessageFormat.format("Message sent. message: {0}.", message))
    }

    @SqsListener(value = ["planet-delete.fifo"], deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    fun processDeleteEventMessages(message: String?, @Header("SenderId") senderId: String?) {
        log.info(MessageFormat.format("SQS Planet Delete Message. senderId: {0}. messageBody: {1} ", senderId, message))
    }

}