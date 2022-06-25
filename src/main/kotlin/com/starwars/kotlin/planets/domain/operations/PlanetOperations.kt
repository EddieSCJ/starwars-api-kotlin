package com.starwars.kotlin.planets.domain.operations

import com.starwars.kotlin.planets.domain.model.Planet
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PlanetOperations {
    fun findAll(page: Int?, size: Int?): Flux<Planet>
    fun findById(id: String, cacheInDays: Long): Mono<Planet>
    fun findByName(name: String, cacheInDays: Long): Mono<Planet>
    fun updateById(id: String, planet: Planet): Mono<Planet>
    fun save(planet: Planet): Mono<Planet>
    fun deleteById(id: String): Mono<Planet>
}