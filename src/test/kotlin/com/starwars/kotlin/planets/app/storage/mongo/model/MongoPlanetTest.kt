package com.starwars.kotlin.planets.app.storage.mongo.model

import com.starwars.kotlin.planets.domain.model.Planet
import commons.base.AbstractIntegrationTest
import commons.utils.DomainUtils
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

internal class MongoPlanetTest {
    @Test
    fun `Deve fazer a conversao para o dominio corretamente`() {
        val mongoPlanet: MongoPlanet = DomainUtils.randomMongoPlanet
        val expectedDomain = Planet(
            mongoPlanet.id.toString(),
            mongoPlanet.name,
            mongoPlanet.climate,
            mongoPlanet.terrain,
            mongoPlanet.movieAppearances,
            Duration.between(LocalDateTime.now(), mongoPlanet.creationDate).toDays()
        )
        Assertions.assertTrue(Objects.deepEquals(expectedDomain, mongoPlanet.toDomain()))
    }

    @Test
    fun `Deve fazer a conversao a partir do dominio corretamente`() {
        val planet: Planet = DomainUtils.randomPlanet
        val (id, name, climate, terrain, movieAppearances) = MongoPlanet(
            ObjectId(planet.id),
            planet.name,
            planet.climate,
            planet.terrain,
            planet.movieAppearances,
            LocalDateTime.now()
        )

        val (id1, name1, climate1, terrain1, movieAppearances1) = MongoPlanet.fromDomain(planet)
        assertEquals(id, id1)
        assertEquals(name, name1)
        assertEquals(movieAppearances, movieAppearances1)
        assertEquals(climate.size, climate1.size)
        assertEquals(terrain.size, terrain1.size)
    }
}