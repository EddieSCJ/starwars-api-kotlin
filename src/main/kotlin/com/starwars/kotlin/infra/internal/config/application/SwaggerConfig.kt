package com.starwars.kotlin.infra.internal.config.application

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.PathProvider
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.Tag
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.paths.DefaultPathProvider
import springfox.documentation.spring.web.plugins.Docket


@Configuration
class SwaggerConfig {

    @Bean
    fun docket(): Docket? {
        return Docket(DocumentationType.OAS_30)
            .apiInfo(
                ApiInfoBuilder()
                    .title("Starwars API")
                    .description("Star wars api client consumer and crud to manage star wars data.")
                    .version("0.0.1-SNAPSHOT")
                    .license("MIT")
                    .licenseUrl("https://opensource.org/licenses/MIT")
                    .build()
            )
            .pathMapping("/api/v0/")
            .select().apis(RequestHandlerSelectors.withClassAnnotation(RestController::class.java))
            .build()
    }
}