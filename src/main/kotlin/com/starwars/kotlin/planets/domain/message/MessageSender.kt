package com.starwars.kotlin.planets.domain.message

interface MessageSender {
    fun sendMessage(message: String)
}