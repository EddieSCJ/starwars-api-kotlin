package com.starwars.kotlin.planets.domain.message

interface NotificationSender {
    fun sendNotification(subject: String, notification: String)
}