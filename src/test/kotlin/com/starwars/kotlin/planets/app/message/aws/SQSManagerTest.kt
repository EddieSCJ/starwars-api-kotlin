package com.starwars.kotlin.planets.app.message.aws

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyMap
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate

internal class SQSManagerTest {
    private var queueMessagingTemplate: QueueMessagingTemplate? = null
    private var sqsManager: SQSManager? = null

    @BeforeEach
    fun setup() {
        queueMessagingTemplate = Mockito.mock(QueueMessagingTemplate::class.java)
        sqsManager = SQSManager(queueMessagingTemplate!!, "testUrl")
    }

    @Test
    fun `Send sqs messages should work well`() {
        sqsManager!!.sendMessage("testando")
        Mockito.verify<QueueMessagingTemplate>(queueMessagingTemplate, Mockito.times(1)).convertAndSend<Any>(anyString(), any(), anyMap())
    }
}