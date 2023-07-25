package com.mlesniak.order.repository

import com.mlesniak.order.service.model.ProductOrder
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document

@Document("orders")
data class OrderDocument(
    @Id
    val id: ObjectId? = null,
    @Version
    val version: Long? = null,

    val orders: List<Order>
) {
    data class Order(
        val productId: Long,
        val quantity: Long,
    ) {
        fun toProductOrder(): ProductOrder =
            ProductOrder(this.productId, this.quantity)
    }
}

fun ProductOrder.toOrder(): OrderDocument.Order =
    OrderDocument.Order(this.productId, this.quantity)


