package com.starwars.kotlin.planets.domain.model.client

data class PlanetResponseJson(
    val count: Int,
    val results: List<PlanetJson>
)