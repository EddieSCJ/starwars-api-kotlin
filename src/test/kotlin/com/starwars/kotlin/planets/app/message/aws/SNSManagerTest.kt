package com.starwars.kotlin.planets.app.message.aws

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate

internal class SNSManagerTest {
    private var notificationMessagingTemplate: NotificationMessagingTemplate? = null
    private var snsManager: SNSManager? = null

    @BeforeEach
    fun setup() {
        notificationMessagingTemplate = Mockito.mock(NotificationMessagingTemplate::class.java)
        snsManager = SNSManager(notificationMessagingTemplate!!)
    }

    @Test
    fun `Send delete notification should work well`() {
        snsManager?.sendNotification("testando", "testando")
        Mockito.verify<NotificationMessagingTemplate>(notificationMessagingTemplate, Mockito.times(1))
            .sendNotification(anyString(), anyString(), anyString())
    }
}