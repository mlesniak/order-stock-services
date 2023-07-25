package com.mlesniak.order.controller.model

// REMARK While the request body for a restock might contain only
//        a single attribute, it still seems cleaner to use a
//        dedicated payload object than additional query parameters.
data class RestockRequest(
    val quantity: Long
) {
    init {
        // REMARK This is a simple example of validation. We could
        //        add more validations in all the other model classes.
        //        (But won't do it for this exemplary service).
        require(quantity > 0) { "Quantity must be positive" }
    }
}