package com.example.travelsidequests

import com.example.travelsidequests.service.FakeImageStorage
import com.example.travelsidequests.service.ImageStorage
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.postgresql.PostgreSQLContainer

/**
 * One Postgres container for the whole suite. Starting it per test class turns a few
 * seconds of setup into minutes.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(AbstractIntegrationTest.TestBeans::class)
abstract class AbstractIntegrationTest {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var imageStorage: ImageStorage

    @BeforeEach
    fun resetStorage() {
        (imageStorage as FakeImageStorage).clear()
    }

    @TestConfiguration
    class TestBeans {
        @Bean
        @Primary
        fun fakeImageStorage(): ImageStorage = FakeImageStorage()
    }

    companion object {
        @JvmStatic
        private val postgres: PostgreSQLContainer =
            PostgreSQLContainer("postgres:18-alpine").apply { start() }

        @JvmStatic
        @DynamicPropertySource
        fun datasourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }
}
