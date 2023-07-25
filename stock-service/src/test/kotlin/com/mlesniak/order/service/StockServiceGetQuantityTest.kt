package com.mlesniak.order.service

import com.mlesniak.order.repository.StockDocument
import com.mlesniak.order.repository.StockRepository
import com.mlesniak.order.service.model.ProductId
import io.mockk.every
import io.mockk.mockk
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// REMARK We intentionally present here just the skeleton for tests.
//        In general, we should test all methods of the service.
//        Philosophically speaking, we can argue to do BDD-based
//        testing or more unit-based ones. In the end, it is a
//        matter of taste and the team's decision.
class StockServiceGetQuantityTest {
    private val stockRepository = mockk<StockRepository>()
    private val sut = StockService(stockRepository)

    @Test
    fun `return correct quantity for an existing stock`() {
        every { stockRepository.findByProductId(1L) } returns StockDocument(
            id = ObjectId(),
            productId = 1L,
            version = 1L,
            quantity = 10,
        )

        val quantity = sut.getQuantity(ProductId(1L))

        assertEquals(10, quantity)
    }

    @Test
    fun `verify exception for non-existing product`() {
        every { stockRepository.findByProductId(1L) } returns null

        val quantity = sut.getQuantity(ProductId(1L))

        assertEquals(null, quantity)
    }
}