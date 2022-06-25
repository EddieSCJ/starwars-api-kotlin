package com.starwars.kotlin.planets.domain.model.view

import com.starwars.kotlin.planets.domain.model.event.OrderedEvent
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Ordered Events are made in case we need maintain sending message order
 * Once we need it, the key parameter is the key of the process to maintain
 * Because it make all messages go to same partition
 */
@Serializable
data class OrderedEventView(
    val key: String,
    val event: EventView
) {
    fun toJson(): String {
        return Json.encodeToString(serializer(), this)
    }

    companion object {
        fun fromDomain(orderedEvent: OrderedEvent): OrderedEventView {
            val eventView = EventView.fromDomain(orderedEvent.event)
            return OrderedEventView(key = orderedEvent.key, event = eventView)
        }
    }
}