package com.starwars.kotlin.planets.app.message.aws

import com.starwars.kotlin.planets.domain.message.NotificationSender
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate
import org.springframework.stereotype.Service
import java.text.MessageFormat

@Service
class SNSManager constructor(private val notificationMessagingTemplate: NotificationMessagingTemplate) :
    NotificationSender {

    val log: Logger = LoggerFactory.getLogger(this::class.java)
    override fun sendNotification(subject: String, notification: String) {
        log.info(MessageFormat.format("Sending notification via SNS. subject: {0}. message: {1}.", subject, notification))
        notificationMessagingTemplate.sendNotification("starwars-planet-delete", notification, "Planet being deleted")
        log.info(MessageFormat.format("Notification sent via SNS. subject: {0}. message: {1}", subject, notification))
    }
}