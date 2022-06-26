package com.starwars.kotlin.planets.app.handler

import com.starwars.kotlin.planets.domain.model.view.PlanetJson
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface IPlanetHandler {

    @GetMapping
    @ApiResponse(
        responseCode = "200", content =
        [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = PlanetJson::class))]
    )
    fun getAll(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "15") size: Int,
        request: ServerHttpRequest
    ): Flux<PlanetJson>

    @GetMapping("/{id}")
    @ApiResponse(
        responseCode = "200",
        content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = PlanetJson::class))]
    )
    fun getByID(
        @PathVariable id: String,
        @RequestParam(name = "cacheInDays", defaultValue = "0") cacheInDays: Long,
        request: ServerHttpRequest
    ): Mono<PlanetJson>

    @GetMapping("/search")
    @ApiResponse(
        responseCode = "200",
        content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = PlanetJson::class))]
    )
    fun getByName(
        @RequestParam(defaultValue = "") name: String,
        @RequestParam(defaultValue = "0") cacheInDays: Long,
        request: ServerHttpRequest
    ): Mono<PlanetJson>

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(
        responseCode = "204",
        content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = PlanetJson::class))]
    )
    fun updateById(@PathVariable id: String, @RequestBody planetJson: PlanetJson, request: ServerHttpRequest): Mono<PlanetJson>

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(
        responseCode = "201",
        content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = Schema(implementation = PlanetJson::class))]
    )
    fun post(planet: PlanetJson, request: ServerHttpRequest): Mono<PlanetJson>

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponse(responseCode = "204", content = [Content(mediaType = MediaType.APPLICATION_JSON_VALUE)])
    fun delete(@PathVariable id: String, request: ServerHttpRequest): Mono<PlanetJson>
}