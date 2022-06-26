package com.starwars.kotlin.planets.app.storage.mongo

import com.starwars.kotlin.planets.app.storage.mongo.model.MongoPlanet
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Flux

interface IPlanetMongoRepository : ReactiveSortingRepository<MongoPlanet, String> {
    fun findAllByIdNotNull(page: Pageable): Flux<MongoPlanet>
}