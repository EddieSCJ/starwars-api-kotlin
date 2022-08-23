package com.starwars.kotlin.planets.app.services

import com.starwars.kotlin.common.exceptions.error.BadRequestError
import com.starwars.kotlin.common.exceptions.error.ConflictError
import com.starwars.kotlin.common.exceptions.error.NotFoundError
import com.starwars.kotlin.planets.app.storage.mongo.IPlanetMongoRepository
import com.starwars.kotlin.planets.app.storage.mongo.model.MongoPlanet
import com.starwars.kotlin.planets.app.validations.PlanetValidator
import com.starwars.kotlin.planets.domain.client.StarWarsApiClient
import com.starwars.kotlin.planets.domain.model.Planet
import com.starwars.kotlin.planets.domain.operations.PlanetOperations
import com.starwars.kotlin.planets.domain.storage.PlanetStorage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.text.MessageFormat.format

@Service
class PlanetService constructor(
    private val starWarsApiClient: StarWarsApiClient,
    private val planetMongoRepository: IPlanetMongoRepository,
    private val planetRepository: PlanetStorage,
    private val planetValidator: PlanetValidator
) : PlanetOperations {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun findAll(page: Int, size: Int): Flux<Planet> {
        val sort = Sort.by(Sort.Direction.ASC, "id")
        return planetRepository.count().flatMapMany { counter ->
            if (counter == 0L) findFromStarWarsClient(size.toLong())
            else planetMongoRepository
                .findAllByIdNotNull(PageRequest.of(page, size).withSort(sort))
                .map { mongoPlanet -> mongoPlanet.toDomain() }
        }
            .switchIfEmpty(Flux.error(NotFoundError("Nenhum planeta encontrado.")))
    }

    override fun findById(id: String, cacheInDays: Long): Mono<Planet> {
        return planetRepository.findById(id)
            .zipWhen {
                if (it.cacheInDays > cacheInDays) {
                    findFromStarWarsClientBy(it.name, it.id)
                        .switchIfEmpty(Mono.error(NotFoundError(format("Planeta não encontrado com o id: {0}", id))))
                        .doOnSuccess { planet -> planetRepository.save(Mono.just(planet)).subscribe() }
                } else Mono.just(it)
            }
            .map { it.t2 }
            .switchIfEmpty(Mono.error(NotFoundError(format("Planeta não encontrado com o id: {0}", id))))
    }

    override fun findByName(name: String, cacheInDays: Long): Mono<Planet> {
        return planetRepository.findByName(name)
            .zipWhen {
                if (it.cacheInDays > cacheInDays)
                    return@zipWhen findFromStarWarsClientBy(it.name, it.id)
                        .flatMap { planet ->
                            planetRepository.save(Mono.just(planet))
                        }
                else return@zipWhen Mono.just(it)
            }
            .map { it.t2 }
            .switchIfEmpty {
                findFromStarWarsClientBy(name, null)
                    .switchIfEmpty(Mono.error(NotFoundError(format("Planeta não encontrado com o nome: {0}", name))))
                    .doOnSuccess { planetRepository.save(Mono.just(it)).subscribe() }
            }
    }

    override fun updateById(id: String?, planet: Planet): Mono<Planet> {
        val errorMessages: MutableList<String> = planetValidator.validate(planet)

        if (id == null || id == "null") {
            log.warn("Erro ao atualizar planeta. id {}. name: {}.", planet.id, planet.name)
            errorMessages.add("Campo id nao pode ser nulo.")
            throw BadRequestError(errorMessages)
        }

        if (errorMessages.isNotEmpty()) {
            log.warn("Erro ao atualizar planeta. id {}. name: {}.", planet.id, planet.name)
            return Mono.error(BadRequestError(errorMessages))
        }

        return planetRepository.findById(id)
            .switchIfEmpty(Mono.error(NotFoundError(format("Planeta não encontrado com o id: {0}", id))))
            .zipWhen {
                val newPlanet = Planet(id, planet.name, planet.climate, planet.terrain, planet.movieAppearances, 0L)
                planetRepository.save(Mono.just(newPlanet))
            }
            .map { tuple -> tuple.t2 }
    }

    @Throws(BadRequestError::class)
    override fun save(planet: Planet): Mono<Planet> {
        val errorMessages: MutableList<String> = planetValidator.validate(planet)

        if (errorMessages.isNotEmpty()) {
            log.warn("Erro ao salvar planeta. name: {}. errors: {}", planet.name, errorMessages)
            return Mono.error(BadRequestError(errorMessages))
        }

        if (planet.id != null) {
            log.warn("Erro ao salvar planeta, pois contém o ID. name: {}. id: {}", planet.name, planet.id)
            errorMessages.add("Campo id nao pode ser preenchido.")
            return Mono.error(BadRequestError(errorMessages))
        }

        return planetRepository.findByName(planet.name)
            .flatMap<Planet> {
                return@flatMap Mono.error(ConflictError(format("Planeta com o nome: {0} ja existe.", planet.name)))
            }
            .switchIfEmpty(planetRepository.save(Mono.just(planet)))
    }

    override fun deleteById(id: String): Mono<Planet> {
        return planetRepository.deleteById(id)
    }

    fun findFromStarWarsClient(size: Long): Flux<Planet> {
        return starWarsApiClient.planets
            .zipWhen { planetResponseJson ->
                if (planetResponseJson.results.isEmpty()) {
                    log.info("Busca de planetas na api do star wars não retornou nenhum resultado.")
                     return@zipWhen Mono.error(NotFoundError("Nenhum planeta encontrado."))
                }
                return@zipWhen Mono.just(planetResponseJson)
            }
            .map { it.t2 }
            .map { planetResponseJson ->
                planetResponseJson.results.parallelStream()
                    .limit(size)
                    .map { planetJson -> planetJson.toDomain(null) }
                    .map(MongoPlanet::fromDomain).toList()
            }
            .flatMapMany { mongoPlanets ->
                planetMongoRepository.saveAll(mongoPlanets).map(MongoPlanet::toDomain)
            }
    }

    private fun findFromStarWarsClientBy(name: String, id: String?): Mono<Planet> {
        return starWarsApiClient.getPlanetBy(name)
            .zipWhen { planetResponseJson ->
                if (planetResponseJson.results.isEmpty()) {
                    log.info("Busca de planetas na api do star wars nao retornou nenhum resultado. id: {}. name: {}.", id, name)
                    return@zipWhen Mono.empty()
                }
                return@zipWhen Mono.just(planetResponseJson.results[0].toDomain(id))
            }
            .map { it.t2 }
            .switchIfEmpty(Mono.empty())
    }
}