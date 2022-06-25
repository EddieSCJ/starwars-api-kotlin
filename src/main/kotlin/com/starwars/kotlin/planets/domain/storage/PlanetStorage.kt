package com.starwars.kotlin.planets.domain.storage

import com.starwars.kotlin.planets.domain.model.Planet
import reactor.core.publisher.Mono

interface PlanetStorage {
    fun count(): Mono<Long>
    fun findByName(name: String): Mono<Planet>
    fun findById(id: String): Mono<Planet>
    fun save(planet: Mono<Planet>): Mono<Planet>
    fun deleteById(id: String): Mono<Planet>
}