package com.starwars.kotlin.planets.app.handler

import com.starwars.kotlin.infra.log.LoggerUtils
import com.starwars.kotlin.planets.domain.model.Planet
import com.starwars.kotlin.planets.domain.model.view.PlanetJson
import com.starwars.kotlin.planets.domain.operations.PlanetOperations
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@Tag(name = "Planets")
@RequestMapping(value = [Constants.PLANETS_ENDPOINT])
class PlanetHandler constructor(private val planetService: PlanetOperations) : IPlanetHandler {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun getAll(page: Int, size: Int, request: ServerHttpRequest): Flux<PlanetJson> {
        log.info("Iniciando busca por todos os planetas. page: {}, size: {}.", page, size)
        LoggerUtils.setOperationInfoIntoMDC(OperationsEnum.GET_PLANETS_PAGE, request)

        return planetService.findAll(page, size)
            .flatMap { planet -> Mono.just(PlanetJson.fromDomain(planet)) }
            .doOnNext { log.info("Busca por todos os planetas concluida com sucesso. page: {}, size: {}.", page, size) }
    }

    override fun getByID(id: String, cacheInDays: Long, request: ServerHttpRequest): Mono<PlanetJson> {
        log.info("Iniciando busca de planeta por id. id: {}. cacheInDays: {}.", id, cacheInDays)
        LoggerUtils.setOperationInfoIntoMDC(id, OperationsEnum.GET_PLANET_BY_ID, request)

        return planetService.findById(id, cacheInDays)
            .flatMap { tempPlanet -> Mono.just(PlanetJson.fromDomain(tempPlanet)) }
            .doOnSuccess { log.info("Busca de planeta por id concluida com sucesso. id: {}. cacheInDays: {}.", id, cacheInDays) }
    }

    override fun getByName(name: String, cacheInDays: Long, request: ServerHttpRequest): Mono<PlanetJson> {
        log.info("Iniciando busca de planeta pelo nome. name: {}. cacheInDays: {}.", name, cacheInDays)
        LoggerUtils.setOperationInfoIntoMDC(name, OperationsEnum.GET_PLANET_BY_NAME, request)

        return planetService.findByName(name, cacheInDays)
            .flatMap { tempPlanet -> Mono.just(PlanetJson.fromDomain(tempPlanet)) }
            .doOnSuccess { log.info("Busca de planeta pelo nome concluida com sucesso. name: {}. cacheInDays: {}.", name, cacheInDays) }
    }


    override fun updateById(id: String, planetJson: PlanetJson, request: ServerHttpRequest): Mono<PlanetJson> {
        log.info("Iniciando atualizacao de planeta pelo id. id: {}", id)
        LoggerUtils.setOperationInfoIntoMDC(id, OperationsEnum.UPDATE_PLANET, request)
        return planetService.updateById(id, planetJson.toDomain())
            .doOnSuccess { planet -> log.info("Atualizacao de planeta pelo id concluida com sucesso. id: {}", id) }
            .then(Mono.empty())
    }

    override fun post(planet: PlanetJson, request: ServerHttpRequest): Mono<PlanetJson> {
        log.info("Iniciando cadastro de planeta por nome. name: {}.", planet.name)
        LoggerUtils.setOperationInfoIntoMDC(planet.id, OperationsEnum.DELETE_PLANET, request)

        return planetService.save(planet.toDomain())
            .flatMap { tempPlanet -> Mono.just(PlanetJson.fromDomain(tempPlanet)) }
            .doOnNext { log.info("Cadastro de planeta por nome concluido com sucesso. name: {}.", planet.name) }
    }

    override fun delete(id: String, request: ServerHttpRequest): Mono<PlanetJson> {
        log.info("Iniciando exclusao de planeta pelo id: {}.", id)
        LoggerUtils.setOperationInfoIntoMDC(id, OperationsEnum.DELETE_PLANET, request)
        return planetService.deleteById(id)
            .doOnSuccess { log.info("Exclusao de planeta pelo id concluida com sucesso. id: {}.", id) }
            .then(Mono.empty())
    }
}