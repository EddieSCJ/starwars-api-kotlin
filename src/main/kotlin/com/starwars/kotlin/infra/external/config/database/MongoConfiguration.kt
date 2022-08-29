package com.starwars.kotlin.infra.external.config.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.SpringDataMongoDB
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import java.text.MessageFormat

@Configuration
data class MongoConfiguration(
    @Value("\${spring.data.mongodb.username:}")
    private val username: String,

    @Value("\${spring.data.mongodb.password:}")
    private val password: String,

    @Value("\${spring.data.mongodb.host:localhost}")
    private val host: String,

    @Value("\${spring.data.mongodb.port:27017}")
    private val port: String,

    @Value("\${spring.data.mongodb.database:admin}")
    private val databaseName: String,

    @Value("\${spring.data.mongodb.authSource:admin}")
    private val authSource: String,

    @Value("\${spring.profiles.active:dev}")
    private val profile: String,
) {
    val log: Logger = LoggerFactory.getLogger(this::class.java)


    @Bean
    fun reactiveMongoTemplate(): ReactiveMongoTemplate? {
        return ReactiveMongoTemplate(client(), databaseName)
    }

    @Bean
    fun client(): MongoClient {
        log.info("host: $host")

        val completeUri: String = when {
            StringUtils.isNotBlank(profile) ->
                MessageFormat.format("mongodb://{0}:{1}@{2}:{3}/{4}?authSource={5}",
                    username,
                    password,
                    host,
                    port,
                    databaseName,
                    authSource
                )
            else ->
                MessageFormat.format(
                    "mongodb+srv://{0}:{1}@{2}/{3}?authSource={4}&authMechanism=SCRAM-SHA-1",
                    username,
                    password,
                    host,
                    databaseName,
                    authSource
                )
        }
        val settings = MongoClientSettings.builder().applyConnectionString(ConnectionString(completeUri)).build()
        return MongoClients.create(settings, SpringDataMongoDB.driverInformation())
    }

}