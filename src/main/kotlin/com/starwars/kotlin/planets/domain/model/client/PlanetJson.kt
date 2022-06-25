package com.starwars.kotlin.planets.domain.model.client

import com.starwars.kotlin.planets.domain.model.Planet


data class PlanetJson(
    val name: String,
    val climate: String,
    val terrain: String,
    val films: List<String>,
) {
    fun toDomain(id: String?): Planet {
        return Planet(
            id,
            name,
            climate.replace(" ", "").split(",").toList(),
            terrain.replace(" ", "").split(",").toList(),
            films.size,
            0L
        )
    }
}