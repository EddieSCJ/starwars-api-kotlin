package com.starwars.kotlin

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.web.reactive.config.EnableWebFlux
import springfox.documentation.swagger2.annotations.EnableSwagger2

@EnableWebFlux
@SpringBootApplication
@EnableReactiveMongoRepositories
@OpenAPIDefinition(info = Info(
    title = "StarWars API",
    description = "API responsible for gateway between StarWars API and other services",
    version = "v0",
    contact = Contact(email = "eddieprofessionalmail@gmail.com", name = "Edcleidson de Souza Cardoso Junior")
)
)class StarWarsApiKotlinApplication

fun main(args: Array<String>) {
    runApplication<StarWarsApiKotlinApplication>(*args)
}

