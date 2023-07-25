package com.mlesniak.order.controller.model

data class InsufficientStocksResponse(
    val availableStocks: List<Stock>
) {
    data class Stock(
        val productId: Long,
        val quantity: Long,
    )
}
