package com.mlesniak.order.service.model

data class Stock (
    val productId: ProductId,

    // REMARK A general design question is the decision of modelling primitive values
    //        as primitive types or as cheap objects (here: JVM-inlined value classes).
    //        In this case, we use primitive types, since our overall data model is not
    //        very complex. If we would have many more attributes of a stock of type
    //        Long, explicitly encapsulating an quantity would be a good idea.
    val quantity: Long
)