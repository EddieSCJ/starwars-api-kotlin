package com.starwars.kotlin.planets.app.storage.mongo

import com.fasterxml.jackson.core.JsonProcessingException
import com.starwars.kotlin.common.exceptions.error.InternalServerError
import com.starwars.kotlin.common.exceptions.error.NotFoundError
import com.starwars.kotlin.planets.app.storage.mongo.model.Constants.FIELD_ID
import com.starwars.kotlin.planets.app.storage.mongo.model.Constants.FIELD_NAME
import com.starwars.kotlin.planets.app.storage.mongo.model.MongoPlanet
import com.starwars.kotlin.planets.domain.message.MessageSender
import com.starwars.kotlin.planets.domain.message.NotificationSender
import com.starwars.kotlin.planets.domain.model.Planet
import com.starwars.kotlin.planets.domain.model.event.Event
import com.starwars.kotlin.planets.domain.model.event.EventEnum
import com.starwars.kotlin.planets.domain.model.view.EventView
import com.starwars.kotlin.planets.domain.storage.PlanetStorage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils
import reactor.core.publisher.Mono
import java.text.MessageFormat.format

@Repository
class MongoStorage constructor(
    private val kafkaManager: MessageSender,
    private val snsManager: NotificationSender,
    private val mongoTemplate: ReactiveMongoTemplate,
    @Qualifier("SQSManager") private val sqsManager: MessageSender
) : PlanetStorage {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun count(): Mono<Long> {
        log.info("Iniciando contagem de planetas no banco.")
        return mongoTemplate
            .estimatedCount(MongoPlanet::class.java)
            .doOnSuccess { counter: Long ->
                log.info("Contagem de planetas no banco concluida com sucesso. planetas: {}.", counter)
            }
    }

    override fun findById(id: String): Mono<Planet> {
        log.info("Iniciando busca de planeta no banco pelo id. id: {}.", id)
        return mongoTemplate
            .findById(id, MongoPlanet::class.java)
            .doOnSuccess { log.info("Planeta encontrado no banco. id: {}.", id) }
            .flatMap { mongoPlanet ->
                Mono.just(mongoPlanet.toDomain())
            }
            .doOnSuccess { log.info("Busca de planeta no banco pelo id concluida com sucesso. id: {}.", id) }
    }

    override fun findByName(name: String): Mono<Planet> {
        log.info("Iniciando busca de planeta no banco pelo nome. name: {}.", name)
        val lowercase: Criteria = Criteria.where(FIELD_NAME).`is`(name.lowercase())
        val uppercase: Criteria = Criteria.where(FIELD_NAME).`is`(name.uppercase())
        val capitalized: Criteria = Criteria.where(FIELD_NAME).`is`(StringUtils.capitalize(name))
        val criteria: Criteria = Criteria().orOperator(lowercase, uppercase, capitalized)
        return mongoTemplate
            .findOne(query(criteria), MongoPlanet::class.java)
            .doOnSuccess { log.info("Planeta encontrado no banco. name: {}.", name) }
            .flatMap { mongoPlanet -> Mono.just(mongoPlanet.toDomain()) }
            .doOnSuccess { log.info("Busca de planeta no banco pelo nome concluida com sucesso. name: {}.", name) }
    }

    override fun save(planet: Mono<Planet>): Mono<Planet> {
        val mongoPlanet: Mono<MongoPlanet> = planet.map { receivedPlanet -> MongoPlanet.fromDomain(receivedPlanet) }
        return mongoTemplate
            .save(mongoPlanet)
            .map(MongoPlanet::toDomain)
            .doOnSuccess { p -> log.info("Planeta salvo no banco. id: {}.", p.id) }
    }

    @Transactional(rollbackFor = [Exception::class])
    override fun deleteById(id: String): Mono<Planet> {
        log.info("Iniciando exclusao de planeta no banco pelo id. id: {}.", id)
        val criteria: Criteria = Criteria.where(FIELD_ID).`is`(id)

        return findById(id)
            .switchIfEmpty(
                Mono.error(
                    NotFoundError(
                        format(
                            "Planeta não encontrado para ser deletado. id: {0}.",
                            id
                        )
                    )
                )
            )
            .zipWhen { p: Planet -> mongoTemplate.remove(query(criteria), MongoPlanet::class.java) }
            .doOnSuccess { tuple ->
                if (tuple.t2.deletedCount == 0L) {
                    log.error("Nenhum planeta foi excluido. id: {}.", id)
                    throw InternalServerError(format("Nenhum planeta foi excluido. id: {0}.", id))
                }
                sendEvents(tuple.t1, EventEnum.DELETE)
            }
            .map { tuple -> tuple.t1 }
    }

    private fun sendEvents(planet: Planet, eventEnum: EventEnum) {
        val event = Event("planet", eventEnum, planet.name)
        val json: String = try {
            EventView.fromDomain(event).toJson()
        } catch (exception: JsonProcessingException) {
            throw InternalServerError(exception.message!!)
        }

        sqsManager.sendMessage(json)
        kafkaManager.sendMessage(json)
        snsManager.sendNotification("${eventEnum.name} planet action", json)
    }
}