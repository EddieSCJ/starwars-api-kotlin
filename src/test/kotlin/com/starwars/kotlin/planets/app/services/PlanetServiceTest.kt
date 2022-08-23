package com.starwars.kotlin.planets.app.services

import com.starwars.kotlin.common.exceptions.error.BadRequestError
import com.starwars.kotlin.common.exceptions.error.NotFoundError
import com.starwars.kotlin.planets.app.storage.mongo.IPlanetMongoRepository
import com.starwars.kotlin.planets.app.storage.mongo.model.MongoPlanet
import com.starwars.kotlin.planets.app.validations.PlanetValidator
import com.starwars.kotlin.planets.domain.client.StarWarsApiClient
import com.starwars.kotlin.planets.domain.model.Planet
import com.starwars.kotlin.planets.domain.model.client.PlanetResponseJson
import com.starwars.kotlin.planets.domain.storage.PlanetStorage
import commons.utils.DomainUtils.FAKE_ID
import commons.utils.DomainUtils.emptyPlanetResponseJson
import commons.utils.DomainUtils.invalidPlanet
import commons.utils.DomainUtils.randomMongoPlanet
import commons.utils.DomainUtils.randomMongoPlanetList
import commons.utils.DomainUtils.randomPlanet
import commons.utils.DomainUtils.randomPlanetResponseJson
import commons.utils.DomainUtils.randomWithoutIdPlanet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.doThrow
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.io.IOException
import java.time.LocalDateTime

internal class PlanetServiceTest {
    @Mock
    lateinit var planetMongoRepository: IPlanetMongoRepository
    @Mock
    lateinit var planetRepository: PlanetStorage
    @Mock
    lateinit var starWarsApiClient: StarWarsApiClient
    @Mock
    lateinit var planetValidator: PlanetValidator
    @InjectMocks
    lateinit var planetService: PlanetService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Nested
    internal inner class FindAll {
        @Test
        fun `Deve retornar uma lista nao vazia quando execetuar um findAll na base de dados`() {
            val list = randomMongoPlanetList
            val resultFlux: Flux<MongoPlanet> = Flux.fromIterable(list)

            `when`(planetRepository.count()).thenReturn(Mono.just(3L))
            `when`(planetMongoRepository.findAllByIdNotNull(anyOrNull()))
                .thenReturn(resultFlux)

            StepVerifier.create(planetService.findAll(1, 15))
                .expectNextSequence(list.map { it.toDomain() })
                .expectComplete()
                .verify()
        }

        @Test
        fun `Deve retornar uma lista vazia da database e executar com sucesso um find na api do star wars`() {
            val planetResponseJson: PlanetResponseJson = randomPlanetResponseJson
            val mongoPlanets: List<MongoPlanet> = planetResponseJson.results
                .map { mPlanetJson -> mPlanetJson.toDomain(null) }
                .map(MongoPlanet::fromDomain)
                .toList()
            `when`(planetRepository.count()).thenReturn(Mono.just(0L))
            `when`(starWarsApiClient.planets).thenReturn(Mono.just(planetResponseJson))
            `when`(planetMongoRepository.saveAll(anyList()))
                .thenReturn(Flux.fromIterable(mongoPlanets))
            StepVerifier.create(planetService.findAll(0, 15))
                .expectNextSequence(mongoPlanets.parallelStream().map { it.toDomain() }.toList())
                .expectComplete()
                .verify()
        }

        @Test
        fun `Deve estourar 404 quando nao encontrar nada na api de star wars e a base de dados estiver vazia`() {
            val planetResponseJson: PlanetResponseJson = emptyPlanetResponseJson
            `when`(planetRepository.count()).thenReturn(Mono.just(0L))
            `when`(starWarsApiClient.planets).thenReturn(Mono.just(planetResponseJson))

            StepVerifier.create(planetService.findAll(0, 15))
                .expectError(NotFoundError::class.java)
                .verify()
        }
    }

    @Nested
    internal inner class FindById {
        @Test
        fun `Deve retornar planeta com sucesso`() {
            val storedPlanet = randomPlanet
            `when`(planetRepository.findById(FAKE_ID)).thenReturn(Mono.just(storedPlanet))
            StepVerifier.create(planetService.findById(FAKE_ID, 0L))
                .expectNext(storedPlanet)
                .expectComplete()
                .verify()

        }

        @Test
        fun `Deve estourar 404 quando nao encontrar nenhum planeta na base de dados`() {
            `when`(planetRepository.findById(FAKE_ID)).thenReturn(Mono.empty())
            StepVerifier.create(planetService.findById(FAKE_ID, 0L))
                .expectError(NotFoundError::class.java)
                .verify()
        }

        @Test
        @Throws(IOException::class, InterruptedException::class)
        fun `Deve buscar e retornar um planeta da api de star wars quando o cache do planeta encontrado na base for invalido`() {
            val mongoPlanet: MongoPlanet = randomMongoPlanet
            mongoPlanet.creationDate = LocalDateTime.of(2001, 12, 12, 12, 12)

            val planetResponseJson: PlanetResponseJson = randomPlanetResponseJson

            val apiClientPlanet: Planet = planetResponseJson.results[0].toDomain(mongoPlanet.id.toString())
            val savedPlanet: MongoPlanet = MongoPlanet.fromDomain(apiClientPlanet)

            `when`(planetRepository.findById(FAKE_ID)).thenReturn(Mono.just(mongoPlanet.toDomain()))
            `when`(planetRepository.save(any())).thenReturn(Mono.just(savedPlanet.toDomain()))
            `when`(starWarsApiClient.getPlanetBy(mongoPlanet.name)).thenReturn(Mono.just(planetResponseJson))

            StepVerifier.create(planetService.findById(FAKE_ID, 0L))
                .expectNext(savedPlanet.toDomain())
                .expectComplete()
                .verify()
        }

        @Test
        @Throws(IOException::class, InterruptedException::class)
        fun `Deve buscar e estourar 404 quando nao encontrar na api de star wars e o cache do planeta na base for invalido`() {
            val mongoPlanet: MongoPlanet = randomMongoPlanet
            val planetResponseJson: PlanetResponseJson = emptyPlanetResponseJson
            mongoPlanet.creationDate = LocalDateTime.of(2001, 12, 12, 12, 12)

            `when`(planetRepository.findById(FAKE_ID)).thenReturn(Mono.just(mongoPlanet.toDomain()))
            `when`(starWarsApiClient.getPlanetBy(mongoPlanet.name)).thenReturn(Mono.just(planetResponseJson))

            StepVerifier.create(planetService.findById(FAKE_ID, 0L))
                .expectError(NotFoundError::class.java)
                .verify()
        }
    }

    @Nested
    internal inner class FindByName {
        @Test
        fun `Deve retornar planeta com sucesso`() {
            val planetExpected = randomPlanet
            `when`(planetRepository.findByName(planetExpected.name)).thenReturn(Mono.just(planetExpected))

            StepVerifier.create(planetService.findByName(planetExpected.name, 0L))
                .expectNext(planetExpected)
                .expectComplete()
                .verify()
        }

        @Test
        @Throws(IOException::class, InterruptedException::class)
        fun `Deve retornar planeta com sucesso quando receber um Optional vazio da database e um resultado nao vazio da api de star wars`() {
            val planetResponseJson: PlanetResponseJson = randomPlanetResponseJson
            val mongoPlanet: MongoPlanet = MongoPlanet.fromDomain(planetResponseJson.results[0].toDomain(FAKE_ID))
            mongoPlanet.creationDate = LocalDateTime.of(2021, 12, 12, 12, 12)

            `when`(planetRepository.findByName(mongoPlanet.name)).thenReturn(Mono.just(mongoPlanet.toDomain()))
            `when`(starWarsApiClient.getPlanetBy(mongoPlanet.name)).thenReturn(Mono.just(planetResponseJson))
            `when`(planetRepository.save(any())).thenReturn(Mono.just(mongoPlanet.toDomain()))
        }

        @Test
        @Throws(IOException::class, InterruptedException::class)
        fun `Deve retornar 404 quando não encontrar planeta com cache valido e não tiver nenhum no client`() {
            val FAKE_NAME = "fake_name"
            val planetResponseJson: PlanetResponseJson = emptyPlanetResponseJson
            `when`(planetRepository.findByName(FAKE_NAME)).thenReturn(Mono.empty())
            `when`(starWarsApiClient.getPlanetBy(FAKE_NAME)).thenReturn(Mono.just(planetResponseJson))

            StepVerifier.create(planetService.findByName(FAKE_NAME, 0L))
                .expectError(NotFoundError::class.java)
                .verify()
        }
    }

    @Nested
    internal inner class UpdateById {
        @Test
        fun `Deve atualizar planeta por id com sucesso`() {
            val oldPlanet = randomPlanet
            val newPlanet = randomPlanet
            `when`(planetValidator.validate(any())).thenReturn(ArrayList())
            `when`(planetRepository.findById(newPlanet.id!!)).thenReturn(Mono.just(oldPlanet))
            `when`(planetRepository.save(any())).thenReturn(Mono.just(newPlanet))

            StepVerifier.create(planetService.updateById(newPlanet.id, newPlanet))
                .expectNext(newPlanet)
                .expectComplete()
                .verify()
        }

        @Test
        fun `Deve estourar 400 ao validar planeta`() {
            val invalidPlanet = invalidPlanet
            `when`(planetValidator.validate(any()))
                .thenReturn(listOf("Campo X nao pode ser nulo.") as MutableList<String>)

            StepVerifier.create(planetService.updateById(FAKE_ID, invalidPlanet))
                .expectError(BadRequestError::class.java)
                .verify()

        }

        @Test
        fun `Deve estourar 404 quando nao encontrar planeta para ser atualizado por id`() {
            `when`(planetRepository.findById(FAKE_ID)).thenReturn(Mono.empty())

            StepVerifier.create(planetService.updateById(FAKE_ID, randomPlanet))
                .expectError(NotFoundError::class.java)
                .verify()
        }
    }

    @Nested
    internal inner class Save {
        @Test
        fun `Deve salvar planeta com sucesso`() {
            val storedPlanet = randomPlanet
            val willBeStored = randomWithoutIdPlanet

            `when`(planetValidator.validate(any())).thenReturn(ArrayList())
            `when`(planetRepository.findByName(anyString())).thenReturn(Mono.empty())
            `when`(planetRepository.save(any())).thenReturn(Mono.just(storedPlanet))

            StepVerifier.create(planetService.save(willBeStored))
                .expectNext(storedPlanet)
                .expectComplete()
                .verify()
        }

        @Test
        fun `Deve estourar 400 ao validar planeta`() {
            `when`(planetValidator.validate(any())).thenReturn(listOf("Campo X nao deve ser vazio") as MutableList<String>)
            StepVerifier.create(planetService.save(invalidPlanet))
                .expectError(BadRequestError::class.java)
                .verify()
        }
    }

    @Nested
    internal inner class Delete {
        @Test
        fun `Deve deletar com sucesso por id`() {
            val planet = randomPlanet
            `when`(planetRepository.deleteById(anyString())).thenReturn(Mono.just(planet))

            StepVerifier.create(planetService.deleteById(FAKE_ID))
                .expectNext(planet)
                .expectComplete()
                .verify()
        }

        @Test
        fun `Deve replicar excecao do metodo delete do repository`() {
            doThrow(NotFoundError::class.java).`when`(planetRepository).deleteById(anyString())
            Assertions.assertThrows(NotFoundError::class.java) { planetService.deleteById(FAKE_ID).subscribe() }
            verify(planetRepository, times(1)).deleteById(anyString())
        }
    }
}