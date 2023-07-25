package com.mlesniak.order.service.model

// REMARK Note that we use this class in the controller as well.
//        This is done for simplicity but also to exemplify that
//        we can use the same model for outgoing and internal
//        communication. One has to be extra careful to not leak
//        technical details into the model, or, leak implementation
//        details into the API.
data class ProductOrder(
    val productId: Long,
    val quantity: Long
)
