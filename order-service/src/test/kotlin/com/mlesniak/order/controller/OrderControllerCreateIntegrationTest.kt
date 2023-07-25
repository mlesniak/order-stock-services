package com.mlesniak.order.controller

import com.github.tomakehurst.wiremock.client.WireMock
import com.mlesniak.order.BaseIntegrationTest
import com.mlesniak.order.controller.model.OrderRequest
import com.mlesniak.order.repository.OrderDocument
import com.mlesniak.order.repository.OrderRepository
import com.mlesniak.order.service.OrderService
import com.mlesniak.order.service.model.ProductOrder
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

// REMARK There are obviously many more tests to write here, but this
//        is just to show that the technicalities work and the integration
//        test concept is sound.
@SpringBootTest
@AutoConfigureMockMvc
// REMARK Ideally, we would use a random port here, but I have trouble
//        getting it to work with Spring Boot and WireMock (for now).
//        In a real-world application, we would use a random port to
//        avoid clashes with other services on the same machine.
@AutoConfigureWireMock(port = 18080)
class OrderControllerCreateIntegrationTest : BaseIntegrationTest() {
    @MockK
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var orderService: OrderService

    @BeforeEach
    fun beforeEach() {
        // See comment in [replaceRepository] for details.
        orderService.replaceRepository(orderRepository)
    }

    // REMARK In general, these are quite technical tests.
    //        In a real-world application, we usually come up with
    //        many helper functions to hide the complexity and make
    //        the tests more readable and closer aligned to the
    //        business domain.
    @Test
    fun `create an order which succeeds`() {
        // We are not interested in the list of saved orders
        // but only want the id of the created order.
        every { orderRepository.save(any()) } returns OrderDocument(
            id = ObjectId("123456789012345678901234"),
            orders = emptyList()
        )

        // Stock service returns a 200 with an empty body
        // on successful reservations.
        WireMock.stubFor(
            WireMock.post("/stock/reserve")
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(200)
                )
        )

        val orders = OrderRequest(
            listOf(
                ProductOrder(1L, 10),
                ProductOrder(2L, 20),
            )
        )
        mockMvc.perform(
            post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orders))
        )
            // Order has been successfully created.
            .andExpect(status().isCreated)
            // And we return the id of the created order.
            .andExpect(content().string("""{"data":{"orderId":"123456789012345678901234"}}"""))
    }

    @Test
    fun `handle an order which fails due to insufficient stocks`() {
        // Stock service returns a 422 which signals that the request
        // was syntactically correct but contained semantical errors
        // (in this case, we requested more than was available).
        WireMock.stubFor(
            WireMock.post("/stock/reserve")
                .willReturn(
                    WireMock.jsonResponse(
                        """
                        {
                            "availableStocks": [
                                {
                                    "productId": 1,
                                    "quantity": 5 
                                },
                                {
                                    "productId": 2,
                                    "quantity": 5 
                                }
                            ]
                        }
                        """.trimIndent(),
                        HttpStatus.UNPROCESSABLE_ENTITY.value(),
                    )
                )
        )

        val orders = OrderRequest(
            listOf(
                ProductOrder(1L, 10),
                ProductOrder(2L, 20),
            )
        )
        mockMvc.perform(
            post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orders))
        )
            .andExpect(status().isUnprocessableEntity)
            // REMARK Checking textual descriptions is worth a discussion,
            //        since they are allowed to change and are not very
            //        stable. Here it's more for demonstration purposes.
            .andExpect(content().string("""{"message":"Insufficient stocks"}"""))
    }
}