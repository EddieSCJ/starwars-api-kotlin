package com.starwars.kotlin.planets.app.message.kafka

import com.starwars.kotlin.planets.domain.message.MessageSender
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.text.MessageFormat

@Service
class KafkaManager constructor(private val kafkaTemplate: KafkaTemplate<String, String>) : MessageSender {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    val topicName = "planet-event"

    override fun sendMessage(message: String) {
        log.info(MessageFormat.format("Starting send event via kafka producer. message: {0}", message))
        kafkaTemplate.send(topicName, message)
        log.info(MessageFormat.format("Message sent event kafka producer. message: {0}", message))
    }

    //if you need add group id just type `, groupId="value"`
    @KafkaListener(topics = ["planet-event"])
    fun consumePlanetEvent(message: String?) {
        log.info(MessageFormat.format("Planet event received via kafka. message: {0}", message))
    }
}