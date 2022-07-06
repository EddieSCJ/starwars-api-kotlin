package com.starwars.kotlin.planets.domain.model.view

import com.starwars.kotlin.planets.domain.model.Planet

data class PlanetJson(
    var id: String?,
    val name: String,
    val climate: List<String>,
    val terrain: List<String>,
    val movieAppearances: Int,
    val cacheInDays: Long,
)
{
    fun toDomain(): Planet = Planet(id, name, climate, terrain, movieAppearances, 0)

    companion object {
        fun fromDomain(planet: Planet): PlanetJson {
            return PlanetJson(
                planet.id,
                planet.name,
                planet.climate,
                planet.terrain,
                planet.movieAppearances,
                planet.cacheInDays,
            )
        }
    }
}