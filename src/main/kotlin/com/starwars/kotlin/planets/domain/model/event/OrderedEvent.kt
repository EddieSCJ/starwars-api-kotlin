package com.starwars.kotlin.planets.domain.model.event

/**
 * Ordered Events are made in case we need maintain sending message order
 * Once we need it, the key parameter is the key of the process to maintain
 * Because it make all messages go to same partition
 */
data class OrderedEvent (val key: String, val event: Event)