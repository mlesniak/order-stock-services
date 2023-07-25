package com.mlesniak.order.controller.model

import com.mlesniak.order.service.model.ProductId.Companion.toProductId
import com.mlesniak.order.service.model.Stock

/**
 * DTO for a single stock, used for communication with
 * the client. The name is not ideal, but the more
 * speaking [Stock] is already used for the internal
 * data model.
 */
data class ClientStock(
    val productId: Long,
    val quantity: Long
) {
    fun toStock() = Stock(productId.toProductId(), quantity)
}
