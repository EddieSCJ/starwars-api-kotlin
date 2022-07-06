package com.starwars.kotlin.planets.app.handler

import com.starwars.kotlin.planets.app.storage.mongo.IPlanetMongoRepository
import com.starwars.kotlin.planets.app.storage.mongo.model.MongoPlanet
import com.starwars.kotlin.planets.domain.model.view.PlanetJson
import commons.base.AbstractIntegrationTest
import commons.utils.DomainUtils.invalidPlanetJson
import commons.utils.DomainUtils.randomMongoPlanet
import commons.utils.DomainUtils.randomPlanetJson
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class PlanetHandlerTest @Autowired constructor(
    val planetMongoRepository: IPlanetMongoRepository,
    val webTestClient: WebTestClient
) : AbstractIntegrationTest() {

    @BeforeEach
    fun setup() {
        planetMongoRepository.deleteAll().subscribe()
    }

    private fun saveMongoPlanet(mongoPlanet: MongoPlanet): MongoPlanet {
        return planetMongoRepository.save(mongoPlanet).block()!!
    }

    @Nested
    internal inner class GetAll {
        @Test
        fun `Deve retornar 10 planetas buscados na API de star wars com sucesso devido a base estar vazia`() {
            webTestClient
                .get()
                .uri(Constants.PLANETS_ENDPOINT)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.length()").isEqualTo(10)
        }

        @Test
        fun `Deve retornar 1 planeta buscado na base de dados com sucesso`() {
            val mongoPlanet: MongoPlanet = saveMongoPlanet(randomMongoPlanet)
            val planet: PlanetJson = PlanetJson.fromDomain(mongoPlanet.toDomain())

            webTestClient
                .get()
                .uri(Constants.PLANETS_ENDPOINT)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(planet.id!!)
                .jsonPath("$.[0].name").isEqualTo(planet.name)
                .jsonPath("$.[0].cacheInDays").isEqualTo(planet.cacheInDays)
                .jsonPath("$.[0].movieAppearances").isEqualTo(planet.movieAppearances)
        }

        @Test
        fun `Deve retornar apenas o tamanho definido no size com sucesso`() {
            webTestClient
                .get()
                .uri("${Constants.PLANETS_ENDPOINT}?size=5")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.length()").isEqualTo(5)

        }
    }

    @Nested
    internal inner class GetById {
        @Test
        fun `Deve retornar um planeta buscado na base de dados com sucesso`() {
            val mongoPlanet: MongoPlanet = saveMongoPlanet(randomMongoPlanet)
            webTestClient
                .get()
                .uri("${Constants.PLANETS_ENDPOINT}/${mongoPlanet.id}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.id").isEqualTo(mongoPlanet.id.toString())
                .jsonPath("$.name").isEqualTo(mongoPlanet.name)
                .jsonPath("$.cacheInDays").isEqualTo(0L)
                .jsonPath("$.movieAppearances").isEqualTo(mongoPlanet.movieAppearances)

        }

        @Test
        fun `Deve buscar com sucesso um planeta na API de star wars quando o cache for invalido`() {
            val mongoPlanet: MongoPlanet = randomMongoPlanet
            val newMongoPlanet = MongoPlanet(
                id = mongoPlanet.id,
                name = "Tatooine",
                climate = mongoPlanet.climate,
                terrain = mongoPlanet.terrain,
                movieAppearances = mongoPlanet.movieAppearances,
                creationDate = LocalDateTime.now().minus(2, ChronoUnit.DAYS)
            )

            saveMongoPlanet(newMongoPlanet)
            webTestClient
                .get()
                .uri("${Constants.PLANETS_ENDPOINT}/${mongoPlanet.id}?cacheInDays=0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.id").isEqualTo(newMongoPlanet.id.toString())
                .jsonPath("$.name").isEqualTo(newMongoPlanet.name)
                .jsonPath("$.cacheInDays").isEqualTo(0L)
        }

        @Test
        fun `Deve estourar 404 quando nao encontrar um planeta por id na base de dados`() {
            val id = UUID.randomUUID().toString()
            webTestClient
                .get()
                .uri("${Constants.PLANETS_ENDPOINT}/$id")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
                .expectBody()
                .jsonPath("$.message").isEqualTo("Planeta não encontrado com o id: $id")
        }

        @Test
        fun `Deve estourar 404 quando nao encontrar um planeta na API de star wars`() {
            val mongoPlanet: MongoPlanet = randomMongoPlanet
            mongoPlanet.creationDate = LocalDateTime.now().minus(3, ChronoUnit.DAYS)
            saveMongoPlanet(mongoPlanet)

            webTestClient
                .get()
                .uri("${Constants.PLANETS_ENDPOINT}/${mongoPlanet.id}?cacheInDays=0")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
                .expectBody()
                .jsonPath("$.message").isEqualTo("Planeta não encontrado com o id: ${mongoPlanet.id}")
        }
    }

    @Nested
    internal inner class GetByName {
        @Test
        fun `Deve retornar um planeta buscado na base de dados com sucesso`() {
            val mongoPlanet: MongoPlanet = saveMongoPlanet(randomMongoPlanet)

            webTestClient
                .get()
                .uri("${Constants.PLANETS_ENDPOINT}/search?name=${mongoPlanet.name}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.id").isEqualTo(mongoPlanet.id.toString())
                .jsonPath("$.name").isEqualTo(mongoPlanet.name)
                .jsonPath("$.cacheInDays").isEqualTo(0L)
                .jsonPath("$.movieAppearances").isEqualTo(mongoPlanet.movieAppearances)
        }

        @Test
        fun `Deve buscar com sucesso um planeta na API de star wars quando o cache for invalido`() {
            val mongoPlanet: MongoPlanet = randomMongoPlanet
            val newMongoPlanet = MongoPlanet(
                id = mongoPlanet.id,
                name = "Tatooine",
                climate = mongoPlanet.climate,
                terrain = mongoPlanet.terrain,
                movieAppearances = mongoPlanet.movieAppearances,
                creationDate = LocalDateTime.now().minus(2, ChronoUnit.DAYS)
            )
            saveMongoPlanet(newMongoPlanet)
            webTestClient
                .get()
                .uri("${Constants.PLANETS_ENDPOINT}/search?name=${newMongoPlanet.name}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath("$.id").isEqualTo(newMongoPlanet.id.toString())
                .jsonPath("$.name").isEqualTo(newMongoPlanet.name)
                .jsonPath("$.cacheInDays").isEqualTo(0L)
        }

        @Test
        fun `Deve estourar 404 quando nao encontrar um planeta por nome na base de dados e na api de star wars`() {
            val name = "SomeName"
            webTestClient
                .get()
                .uri("${Constants.PLANETS_ENDPOINT}/search?name=$name")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
                .expectBody()
                .jsonPath("$.message").isEqualTo("Planeta não encontrado com o nome: $name")
        }

        @Test
        fun `Deve estourar 404 quando nao encontrar um planeta na base de dados com cache invalido e nao encontrar na API de star wars`() {
            val mongoPlanet: MongoPlanet = randomMongoPlanet
            mongoPlanet.creationDate = LocalDateTime.now().minus(3, ChronoUnit.DAYS)
            saveMongoPlanet(mongoPlanet)

            webTestClient
                .get()
                .uri("${Constants.PLANETS_ENDPOINT}/search?name=${mongoPlanet.name}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
                .expectBody()
                .jsonPath("$.message").isEqualTo("Planeta não encontrado com o nome: ${mongoPlanet.name}")
        }

    }

    @Nested
    internal inner class UpdateById {
        @Test
        fun `Deve atualizar um planeta encontrado na base de dados com sucesso`() {
            val mongoPlanet: MongoPlanet = saveMongoPlanet(randomMongoPlanet)
            val updatePlanet: PlanetJson = randomPlanetJson
            updatePlanet.id = mongoPlanet.id.toString()

            webTestClient
                .put()
                .uri("${Constants.PLANETS_ENDPOINT}/${updatePlanet.id}")
                .bodyValue(updatePlanet)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent
        }

        @Test
        fun `Deve estourar 404 quando nao encontrar um planeta na base de dados`() {
            val planetJson = randomPlanetJson
            planetJson.id = UUID.randomUUID().toString()

            webTestClient
                .put()
                .uri("${Constants.PLANETS_ENDPOINT}/${planetJson.id}")
                .bodyValue(planetJson)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
                .expectBody()
                .jsonPath("$.message").isEqualTo("Planeta não encontrado com o id: ${planetJson.id}")
        }

        @Test
        fun `Deve estourar 400 quando o planeta tiver campos invalidos`() {
            val planetJson = invalidPlanetJson
            planetJson.id = UUID.randomUUID().toString()

            webTestClient
                .put()
                .uri("${Constants.PLANETS_ENDPOINT}/${planetJson.id}")
                .bodyValue(planetJson)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.errors[0]").isEqualTo("Campo name nao pode ser vazio.")
                .jsonPath("$.errors[1]").isEqualTo("Campo climate nao pode ser vazio.")
                .jsonPath("$.errors[2]").isEqualTo("Campo terrain nao pode ser vazio.")
        }

        @Test
        fun `Deve estourar 400 quando o planeta tiver id invalido`() {
            val planetJson = randomPlanetJson
            planetJson.id = null

            webTestClient
                .put()
                .uri("${Constants.PLANETS_ENDPOINT}/${planetJson.id}")
                .bodyValue(planetJson)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.errors[0]").isEqualTo("Campo id nao pode ser nulo.")
        }
    }

    @Nested
    internal inner class Save {
        @Test
        fun `Deve salvar um planeta na base de dados com sucesso`() {
            val planetJson = randomPlanetJson
            planetJson.id = null

            webTestClient
                .post()
                .uri(Constants.PLANETS_ENDPOINT)
                .bodyValue(planetJson)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.name").isEqualTo(planetJson.name)
                .jsonPath("$.climate.length()").isEqualTo(planetJson.climate.size)
                .jsonPath("$.cacheInDays").isEqualTo(0L)
        }

        @Test
        fun `Deve estourar 400 quando o planeta tiver campos invalidos`() {
            val planetJson = invalidPlanetJson

            webTestClient
                .post()
                .uri(Constants.PLANETS_ENDPOINT)
                .bodyValue(planetJson)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest
                .expectBody()
                .jsonPath("$.errors[0]").isEqualTo("Campo name nao pode ser nulo.")
                .jsonPath("$.errors[1]").isEqualTo("Campo climate nao pode ser nulo.")
                .jsonPath("$.errors[2]").isEqualTo("Campo terrain nao pode ser nulo.")
                .jsonPath("$.errors[3]").isEqualTo("Campo movieAppearances nao pode ser nulo.")
        }

    }

    @Nested
    internal inner class Delete {
        @Test
        fun `Deve deletar um planeta da base de dados pelo id com sucesso`() {
            val mongoPlanet: MongoPlanet = saveMongoPlanet(randomMongoPlanet)

            webTestClient
                .delete()
                .uri("${Constants.PLANETS_ENDPOINT}/${mongoPlanet.id}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent
        }

        @Test
        fun `Deve estourar 404 quando nao encontrar um planeta na base de dados pelo id`() {
            val id = "some-id"
            webTestClient
                .delete()
                .uri("${Constants.PLANETS_ENDPOINT}/$id")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound
                .expectBody()
                .jsonPath("$.message").isEqualTo("Nenhum planeta foi encontrado para ser deletado pelo id: $id.")
        }
    }
}