package com.mlesniak.order.client

data class ReservationRequest(
    val reservations: List<ClientStock>
)