package com.starwars.kotlin.planets.app.storage.mongo.model

import com.starwars.kotlin.planets.domain.model.Planet
import commons.utils.DomainUtils
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

internal class MongoPlanetTest {
    @Test
    @DisplayName("Deve fazer a conversao para o dominio corretamente.")
    fun toDomainSuccessfully() {
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
    @DisplayName("Deve fazer a conversao a partir do dominio corretamente.")
    fun fromDomainSuccessfully() {
        val dateTime = LocalDateTime.now()
        Mockito.mockStatic<LocalDateTime>(LocalDateTime::class.java).use { mock ->
            mock.`when`<Any> { LocalDateTime.now() }.thenReturn(dateTime)

            val planet: Planet = DomainUtils.randomPlanet
            val (id, name, climate, terrain, movieAppearances, creationDate) = MongoPlanet(
                ObjectId(planet.id),
                planet.name,
                planet.climate,
                planet.terrain,
                planet.movieAppearances,
                LocalDateTime.now()
            )

            val (id1, name1, climate1, terrain1, movieAppearances1, creationDate1) = MongoPlanet.fromDomain(planet)
            assertEquals(id, id1)
            assertEquals(creationDate, creationDate1)
            assertEquals(name, name1)
            assertEquals(movieAppearances, movieAppearances1)
            assertEquals(climate.size, climate1.size)
            assertEquals(terrain.size, terrain1.size)
        }
    }
}