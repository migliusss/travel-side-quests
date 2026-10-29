package com.example.travelsidequests.config

import com.example.travelsidequests.service.MinioImageStorage
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.SetBucketLifecycleArgs
import io.minio.messages.Expiration
import io.minio.messages.LifecycleConfiguration
import io.minio.messages.LifecycleRule
import io.minio.messages.RuleFilter
import io.minio.messages.Status
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.ZonedDateTime

@Configuration
@EnableConfigurationProperties(MinioProperties::class)
class MinioConfig {

    private val log = LoggerFactory.getLogger(MinioConfig::class.java)

    @Bean
    fun minioClient(properties: MinioProperties): MinioClient =
        MinioClient.builder()
            .endpoint(properties.endpoint)
            .credentials(properties.accessKey, properties.secretKey)
            .build()

    /**
     * Creates the bucket and tells MinIO to delete anything still sitting in the staging
     * prefix after a day. That is the whole orphan story for abandoned submissions — no
     * cleanup job of our own.
     */
    @Bean
    @ConditionalOnProperty(name = ["minio.bootstrap"], havingValue = "true", matchIfMissing = true)
    fun minioBootstrap(client: MinioClient, properties: MinioProperties) = ApplicationRunner {
        val exists = client.bucketExists(
            BucketExistsArgs.builder().bucket(properties.bucket).build()
        )

        if (!exists) {
            client.makeBucket(MakeBucketArgs.builder().bucket(properties.bucket).build())
            log.info("created bucket {}.", properties.bucket)
        }

        val expirePending = LifecycleRule(
            Status.ENABLED,
            null,
            Expiration(null as ZonedDateTime?, 1, null),
            RuleFilter(MinioImageStorage.PENDING_PREFIX),
            "expire-pending-uploads",
            null,
            null,
            null,
        )

        client.setBucketLifecycle(
            SetBucketLifecycleArgs.builder()
                .bucket(properties.bucket)
                .config(LifecycleConfiguration(listOf(expirePending)))
                .build()
        )
    }
}
