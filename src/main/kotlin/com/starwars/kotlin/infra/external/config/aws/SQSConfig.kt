package com.starwars.kotlin.infra.external.config.aws

import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.context.annotation.Primary
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SQSConfig {
    @Value("\${cloud.aws.region.static}")
    private val region: String? = null

    @Value("\${cloud.aws.credentials.access-key}")
    private val sqsAccessKey: String? = null

    @Value("\${cloud.aws.credentials.secret-key}")
    private val sqsSecretKey: String? = null

    @Value("\${cloud.aws.sqs.endpoint:defaultEndPoint}")
    private val sqsEndPoint: String? = null
    @Bean
    fun queueMessagingTemplate(): QueueMessagingTemplate {
        return QueueMessagingTemplate(amazonSQSAsync())
    }

    @Bean
    @Primary
    fun amazonSQSAsync(): AmazonSQSAsync {
        return if (sqsEndPoint == "defaultEndPoint") {
            AmazonSQSAsyncClientBuilder.standard().withRegion(region)
                .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(sqsAccessKey, sqsSecretKey)))
                .build()
        } else AmazonSQSAsyncClientBuilder.standard()
            .withEndpointConfiguration(EndpointConfiguration(sqsEndPoint, region))
            .withCredentials(AWSStaticCredentialsProvider(BasicAWSCredentials(sqsAccessKey, sqsSecretKey)))
            .build()
    }
}