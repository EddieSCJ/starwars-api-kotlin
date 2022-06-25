package com.starwars.kotlin.infra.internal.config.application

import com.starwars.kotlin.infra.helpers.MessageSourceHelper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import java.nio.charset.StandardCharsets

@Configuration
data class ApplicationConfig(
    @Value("\${clients.starwars.url}")
    private val starWarsUrl: String
) {
    @Bean
    fun messageSourceHelper(): MessageSourceHelper {
        return MessageSourceHelper(reloadableResourceBundleMessageSource())
    }

    @Bean
    fun reloadableResourceBundleMessageSource(): ReloadableResourceBundleMessageSource {
        val reloadableResourceBundleMessageSource = ReloadableResourceBundleMessageSource()
        reloadableResourceBundleMessageSource.setBasename("classpath:messages")
        reloadableResourceBundleMessageSource.setDefaultEncoding(StandardCharsets.UTF_8.name())
        reloadableResourceBundleMessageSource.setUseCodeAsDefaultMessage(true)
        return reloadableResourceBundleMessageSource
    }
}