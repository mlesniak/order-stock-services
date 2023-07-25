package com.mlesniak.order.service.model

import org.bson.types.ObjectId

@JvmInline
value class OrderId(val id: String) {
    override fun toString(): String = id
}

fun ObjectId.toOrderId() = OrderId(this.toString())