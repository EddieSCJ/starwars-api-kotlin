package com.starwars.kotlin.planets.domain.model.view

import com.starwars.kotlin.planets.domain.model.event.Event
import com.starwars.kotlin.planets.domain.model.event.EventEnum
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class EventView(
    val type: String,
    val event: EventEnum,
    val message: String
) {

    fun toJson(): String {
        return Json.encodeToString(serializer(), this)
    }

    companion object {
        @JvmStatic
        fun fromDomain(event: Event): EventView {
            return EventView(type = event.type, event = event.event, message = event.message)
        }
    }
}