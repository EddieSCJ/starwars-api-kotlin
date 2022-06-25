package com.starwars.kotlin.infra.external.config.aws

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sns.AmazonSNSAsync
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class SNSConfig {
    @Value("\${cloud.aws.region.static}")
    private val region: String? = null

    @Value("\${cloud.aws.credentials.access-key}")
    private val sqsAccessKey: String? = null

    @Value("\${cloud.aws.credentials.secret-key}")
    private val sqsSecretKey: String? = null

    @Value("\${cloud.aws.sns.endpoint:defaultEndPoint}")
    private val snsEndPoint: String? = null

    @Bean
    fun notificationMessagingTemplate(amazonSNS: AmazonSNSAsync): NotificationMessagingTemplate {
        return NotificationMessagingTemplate(amazonSNS)
    }

    @Bean
    @Primary
    fun amazonSNSAsync(): AmazonSNSAsync {
        return if (snsEndPoint == "defaultEndPoint") {
            AmazonSNSAsyncClientBuilder.standard().withRegion(region)
                .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(sqsAccessKey, sqsSecretKey)))
                .build()
        } else AmazonSNSAsyncClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration(snsEndPoint, region))
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(sqsAccessKey, sqsSecretKey)))
            .build()
    }
}