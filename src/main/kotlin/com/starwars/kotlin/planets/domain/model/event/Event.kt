package com.starwars.kotlin.planets.domain.model.event

data class Event(val type: String, val event: EventEnum, val message: String)