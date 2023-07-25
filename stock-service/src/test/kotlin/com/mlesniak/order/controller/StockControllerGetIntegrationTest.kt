package com.mlesniak.order.controller

import com.mlesniak.order.BaseIntegrationTest
import com.mlesniak.order.controller.model.ClientStock
import com.mlesniak.order.toDataJson
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class StockControllerGetIntegrationTest : BaseIntegrationTest() {
    @Test
    fun `retrieve a newly created stock`() {
        // REMARK We would test that creation works in a separate
        //        integration test.
        mockMvc.perform(post("/stock/1"))

        mockMvc.perform(get("/stock/1"))
            .andExpect(status().isOk)
            .andExpect(content().json(ClientStock(1L, 0).toDataJson()))
    }
}