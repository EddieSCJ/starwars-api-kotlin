package com.starwars.kotlin

import com.starwars.kotlin.infra.external.config.database.MongoConfiguration
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.web.reactive.config.EnableWebFlux

@EnableWebFlux
@SpringBootApplication
@EnableReactiveMongoRepositories
@SpringBootTest(classes = [StarWarsApiKotlinApplication::class, MongoConfiguration::class])
class StarWarsApiKotlinApplicationTests {

	@Test
	fun contextLoads() {}

}
