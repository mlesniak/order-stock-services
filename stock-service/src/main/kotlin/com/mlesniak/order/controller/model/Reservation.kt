package com.mlesniak.order.controller.model

import com.mlesniak.order.service.model.Stock

data class InsufficientStocksResponse(
    val availableStocks: List<Stock>
)

data class ReservationRequest(
    val reservations: List<ClientStock>
)
