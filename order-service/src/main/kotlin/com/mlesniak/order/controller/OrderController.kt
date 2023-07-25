package com.mlesniak.order.controller

import com.mlesniak.order.controller.model.OrderRequest
import com.mlesniak.order.controller.model.OrderResponse
import com.mlesniak.order.controller.model.ResponseResult
import com.mlesniak.order.controller.model.RestResult.Companion.badRequest
import com.mlesniak.order.controller.model.RestResult.Companion.ok
import com.mlesniak.order.service.OrderService
import com.mlesniak.order.service.model.OrderId
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

// REMARK We are currently not logging request and responses If this is
//        necessary depends on the surrounding infrastructure, i.e. do
//        we have a sidecar, a proxy, ... which accompanies the service?
@RestController
class OrderController(
    private val orderService: OrderService,
) {
    // REMARK Note that we return the technical orderId here. In contrast to the
    //        productId, which we have not created (and thus have no ownership and
    //        use a separate primary key in the respective document / entity), we
    //        have created the orderId and have thus full control over it.
    @PostMapping("/order")
    fun create(@RequestBody orderRequest: OrderRequest): ResponseResult<OrderResponse> {
        val orderId = orderService.create(orderRequest.orders)
            ?: return badRequest(
                message = "Insufficient stocks",
                statusCode = HttpStatus.UNPROCESSABLE_ENTITY
            )
        return ok(OrderResponse(orderId.toString()), HttpStatus.CREATED)
    }

    @GetMapping("/order/{orderId}")
    fun get(@PathVariable orderId: String): ResponseResult<OrderResponse> {
        val orders = orderService.get(OrderId(orderId))
            ?: return badRequest(statusCode = HttpStatus.NOT_FOUND)
        return ok(
            OrderResponse(
                orderId = orderId,
                orders = orders
            )
        )
    }
}
