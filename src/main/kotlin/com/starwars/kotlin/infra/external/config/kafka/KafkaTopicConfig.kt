package com.starwars.kotlin.infra.external.config.kafka

import java.util.HashMap
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.context.annotation.Bean
import org.apache.kafka.clients.admin.AdminClientConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaTopicConfig {
    @Value(value = "\${kafka.bootstrap.address}")
    private val bootstrapAddress: String? = null

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any?> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress

        return KafkaAdmin(configs)
    }

    // If I wanted create topics with code
    //    @Bean
    //    public NewTopic topic1() {
    //        return new NewTopic("baeldung", 1, (short) 1);
    //    }
}