package com.starwars.kotlin.planets.app.message.kafka

import com.starwars.kotlin.planets.domain.message.MessageSender
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.springframework.kafka.core.KafkaTemplate

class KafkaManagerTest {
    var messageSender: KafkaTemplate<String, String>? = null
    var kafkaManager: MessageSender? = null

    @BeforeEach
    fun setup() {
        messageSender = Mockito.mock(KafkaTemplate::class.java) as KafkaTemplate<String, String>?
        kafkaManager = KafkaManager(messageSender!!)
    }

    @Test
    fun `Send kafka messages should work well`() {
        kafkaManager?.sendMessage("testando")
        Mockito.verify<KafkaTemplate<String, String>>(messageSender, Mockito.times(1))
            .send(anyString(), anyString())
    }
}