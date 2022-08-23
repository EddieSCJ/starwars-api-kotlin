package com.starwars.kotlin.planets.domain.client

import com.starwars.kotlin.planets.app.handler.Constants.PLANETS_ENDPOINT
import com.starwars.kotlin.planets.domain.model.client.PlanetResponseJson
import feign.Headers
import feign.Param
import feign.RequestLine
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
@Headers(value = ["Accept: application/json"])
interface StarWarsApiClient {
    @RequestLine("GET $PLANETS_ENDPOINT/?search={search}")
    fun getPlanetBy(@Param("search") name: String): Mono<PlanetResponseJson>

    @get:RequestLine("GET $PLANETS_ENDPOINT")
    val planets: Mono<PlanetResponseJson>
}