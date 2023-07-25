package com.mlesniak.order

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.mlesniak.order.repository.OrderRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
class BaseIntegrationTest {
    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    init {
        mongoDB.start()
        Thread.sleep(1000)
        mongoDB.execInContainer("mongosh", "--eval", "'rs.initiate()'")
    }

    @BeforeEach
    fun cleanup() {
        orderRepository.deleteAll()
        WireMock.reset()
    }

    companion object {
        @Container
        private var mongoDB = GenericContainer("mongo:latest")
            .withCommand("mongod --bind_ip_all --replSet rs0")
            .withExposedPorts(27017)

        @JvmStatic
        @DynamicPropertySource
        fun setProperties(registry: DynamicPropertyRegistry) {
            val uri = "mongodb://localhost:${mongoDB.getMappedPort(27017)}/test"
            registry.add("spring.data.mongodb.uri") { uri }
        }
    }
}
