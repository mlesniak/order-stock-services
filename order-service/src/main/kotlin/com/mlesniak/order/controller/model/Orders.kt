package com.mlesniak.order.controller.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.mlesniak.order.service.model.ProductOrder

data class OrderRequest(
    val orders: List<ProductOrder>
)

// REMARK We use the same model for the response of
//        get and post to simplify our lives here.
//        Ideally, we have different request and
//        response models for every type of request
//        and just share common model types and key
//        pillars (such as the product id).
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class OrderResponse(
    val orderId: String,
    val orders: List<ProductOrder>? = null,
)

