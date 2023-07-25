package com.mlesniak.order.client

import com.mlesniak.order.controller.model.InsufficientStocksResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.POST

data class Data<T>(val data: T? = null)

// REMARK Note that we do not use any kind of Circuit Breaker here
//        for sake of simplicity. In a real-world application, we
//        would these to avoid cascading failures, e.g. by adding
//        Hysterix.
@FeignClient(
    value = "stock-service",
    url = "\${clients.stock-service.url}",
)
interface StockServiceClient {
    // REMARK Interesting design clash between Kotlin and Spring:
    //        Nullable types are a viable alternative to Optional
    //        types (and we use them here). Data Repositories, however,
    //        use Optional types. This might be a bit confusing.
    //        Technically, we could override findById to return a
    //        nullable type, but this would be a bit confusing since
    //        it is not the default.
    @RequestMapping(method = [POST], value = ["/stock/reserve"])
    fun reserveProducts(reserveProducts: ReservationRequest): Data<InsufficientStocksResponse>?
}