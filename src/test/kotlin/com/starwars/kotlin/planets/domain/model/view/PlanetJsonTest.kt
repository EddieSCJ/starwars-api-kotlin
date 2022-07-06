package com.starwars.kotlin.planets.domain.model.view

import com.starwars.kotlin.planets.domain.model.Planet
import commons.utils.DomainUtils.randomPlanet
import commons.utils.DomainUtils.randomPlanetJson
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.util.*

internal class PlanetJsonTest {
    @Test
    fun `Deve fazer a conversao para o dominio corretamente`() {
        val planetJson = randomPlanetJson
        val expectedPlanet = Planet(
            planetJson.id,
            planetJson.name,
            planetJson.climate,
            planetJson.terrain,
            planetJson.movieAppearances,
            planetJson.cacheInDays
        )
        Assertions.assertTrue(Objects.deepEquals(expectedPlanet, planetJson.toDomain()))
    }

    @Test
    fun `Deve fazer a conversao a partir do dominio com sucesso`() {
        val planet = randomPlanet
        val expectedPlanetJson = PlanetJson(
            planet.id,
            planet.name,
            planet.climate,
            planet.terrain,
            planet.movieAppearances,
            planet.cacheInDays
        )
        Assertions.assertTrue(Objects.deepEquals(expectedPlanetJson, PlanetJson.fromDomain(planet)))
    }
}