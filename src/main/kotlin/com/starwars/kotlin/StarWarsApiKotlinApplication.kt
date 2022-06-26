package com.starwars.kotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.web.reactive.config.EnableWebFlux

@EnableWebFlux
@SpringBootApplication
@EnableReactiveMongoRepositories
class StarWarsApiKotlinApplication

fun main(args: Array<String>) {
    runApplication<StarWarsApiKotlinApplication>(*args)
}

