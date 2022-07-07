package com.starwars.kotlin.planets.app.storage.mongo

import com.mongodb.client.result.DeleteResult
import com.starwars.kotlin.common.exceptions.error.NotFoundError
import com.starwars.kotlin.planets.app.storage.mongo.model.Constants.FIELD_ID
import com.starwars.kotlin.planets.app.storage.mongo.model.Constants.FIELD_NAME
import com.starwars.kotlin.planets.app.storage.mongo.model.MongoPlanet
import com.starwars.kotlin.planets.domain.message.MessageSender
import com.starwars.kotlin.planets.domain.message.NotificationSender
import com.starwars.kotlin.planets.domain.model.Planet
import commons.utils.DomainUtils
import commons.utils.DomainUtils.FAKE_ID
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.*
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.util.StringUtils
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class MongoStorageTest() {
    @Mock
    lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @Mock
    @Qualifier("SQSManager")
    lateinit var messageSender: MessageSender

    @Mock
    lateinit var snsManager: NotificationSender

    @InjectMocks
    lateinit var planetRepository: MongoStorage

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    private fun `Get find by name criteria`(FAKE_NAME: String): Criteria {
        val lowercase = Criteria.where(FIELD_NAME).`is`(FAKE_NAME.lowercase())
        val uppercase = Criteria.where(FIELD_NAME).`is`(FAKE_NAME.uppercase())
        val capitalized = Criteria.where(FIELD_NAME).`is`(StringUtils.capitalize(FAKE_NAME))
        return Criteria().orOperator(lowercase, uppercase, capitalized)
    }

    @Nested
    internal inner class Count {
        @Test
        fun `Deve retornar a quantidade de documentos na colecao`() {
            `when`<Mono<Long>>(reactiveMongoTemplate.estimatedCount(MongoPlanet::class.java))
                .thenReturn(Mono.just(0L))

            StepVerifier.create(planetRepository.count())
                .expectNext(0L)
                .expectComplete()
                .verify()

            verify(reactiveMongoTemplate, times(1))
                .estimatedCount(eq(MongoPlanet::class.java))
        }
    }

    @Nested
    internal inner class FindById {
        @Test
        fun `Deve retornar um Mono nao vazio de MongoPlanet da database`() {
            val queryResult: MongoPlanet = DomainUtils.randomMongoPlanet
            val mongoPlanet: Mono<MongoPlanet> = Mono.just(queryResult)
            val planet: Planet = queryResult.toDomain()
            `when`(reactiveMongoTemplate.findById(eq(FAKE_ID), eq(MongoPlanet::class.java)))
                .thenReturn(mongoPlanet)

            StepVerifier
                .create(planetRepository.findById(FAKE_ID))
                .expectNext(planet)
                .expectComplete()
                .verify()

            verify(reactiveMongoTemplate, times(1))
                .findById(eq(FAKE_ID), eq(MongoPlanet::class.java))
        }

        @Test
        fun `Deve estourar 404 quando nao encontrar nenhum planeta com o id passado na database`() {
            `when`(reactiveMongoTemplate.findById(FAKE_ID, MongoPlanet::class.java)).thenReturn(Mono.empty())

            StepVerifier
                .create(planetRepository.findById(FAKE_ID))
                .expectComplete()
                .verify()

            verify(reactiveMongoTemplate, times(1))
                .findById(eq(FAKE_ID), eq(MongoPlanet::class.java))
        }
    }

    @Nested
    internal inner class FindByName {
        @Test
        fun `Deve retornar um Mono nao vazio`() {
            val FAKE_NAME = "fake_name"
            val mongoPlanet: MongoPlanet = DomainUtils.randomMongoPlanet

            val criteria = `Get find by name criteria`(FAKE_NAME)
            `when`(reactiveMongoTemplate.findOne(Query.query(criteria), MongoPlanet::class.java))
                .thenReturn(Mono.just(mongoPlanet))

            StepVerifier
                .create(planetRepository.findByName(FAKE_NAME))
                .expectNext(mongoPlanet.toDomain())
                .expectComplete()
                .verify()

            verify(reactiveMongoTemplate, times(1))
                .findOne(eq(Query.query(criteria)), eq(MongoPlanet::class.java))
        }

        @Test
        fun `Deve estourar 404 quando nao encontrar nenhum planeta na database com o nome passado`() {
            val FAKE_NAME = "fake_name"
            val criteria = `Get find by name criteria`(FAKE_NAME)
            `when`(reactiveMongoTemplate.findOne(Query.query(criteria), MongoPlanet::class.java))
                .thenReturn(Mono.empty())

            StepVerifier
                .create(planetRepository.findByName(FAKE_NAME))
                .expectComplete()
                .verify()

            verify(reactiveMongoTemplate, times(1))
                .findOne(eq(Query.query(criteria)), eq(MongoPlanet::class.java))
        }
    }

    @Nested
    internal inner class Save {
        @Test
        fun `Deve salvar com sucesso um MongoPlanet`() {
            val planet: Planet = DomainUtils.randomPlanet
            val mongoPlanet = MongoPlanet.fromDomain(planet)
            val planetMono: Mono<Planet> = Mono.just(planet)
            val mongoPlanetMono: Mono<MongoPlanet> = planetMono.map { planet -> MongoPlanet.fromDomain(planet)}

            `when`(reactiveMongoTemplate.save(any(Mono::class.java))).thenReturn(Mono.just(mongoPlanet))

            StepVerifier
                .create(planetRepository.save(planetMono))
                .expectNext(planet)
                .expectComplete()
                .verify()
        }
    }

    @Nested
    internal inner class Delete {
        @Test
        fun `Deve buscar por id, encontrar e deletar com sucesso o MongoPlanet`() {
            val criteria = Criteria.where(FIELD_ID).`is`(FAKE_ID)
            `when`(reactiveMongoTemplate.findById(FAKE_ID, MongoPlanet::class.java))
                .thenReturn(Mono.just(DomainUtils.randomMongoPlanet))

            `when`(reactiveMongoTemplate.remove(Query.query(criteria), MongoPlanet::class.java))
                .thenReturn(Mono.just(DeleteResult.acknowledged(1L)))

            StepVerifier
                .create(planetRepository.deleteById(FAKE_ID))
                .expectNextCount(1)
                .expectComplete()
                .verify()

            verify(reactiveMongoTemplate, times(1))
                .remove(Query.query(criteria), MongoPlanet::class.java)
            verify(messageSender, times(2)).sendMessage(anyString())
            verify(snsManager, times(1)).sendNotification(anyString(), anyString())
        }

        @Test
        fun `Deve estourar um 404 ao tentar deletar pois nao existe documento com o id passado`() {
            val criteria = Criteria.where(FIELD_ID).`is`(FAKE_ID)
            `when`(reactiveMongoTemplate.findById(FAKE_ID, MongoPlanet::class.java)).thenReturn(Mono.empty())
            `when`<Mono<DeleteResult>>(reactiveMongoTemplate.remove(Query.query(criteria), MongoPlanet::class.java))
                .thenReturn(Mono.just(DeleteResult.acknowledged(0L)))

            StepVerifier
                .create(planetRepository.deleteById(FAKE_ID))
                .expectError(NotFoundError::class.java)
                .verify()
            verify(reactiveMongoTemplate, times(1))
                .findById(FAKE_ID, MongoPlanet::class.java)
        }
    }
}