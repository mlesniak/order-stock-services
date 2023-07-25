package com.mlesniak.order.repository

import com.mlesniak.order.service.model.ProductId
import com.mlesniak.order.service.model.Stock
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document("stocks")
data class StockDocument(
    // REMARK Note that we do not use the productId as the identifier.
    //        The productId is "owned" by the domain and business
    //        (and we don't have any control over it, e.g. it must not
    //        be 0), while MongoDB's id is technically motivated.
    @Id
    val id: ObjectId? = null,

    // REMARK Prevent race conditions by using optimistic locking when
    //        (for example) updating the stock quantity.
    @Version
    val version: Long? = null,

    @Indexed(unique = true)
    val productId: Long,
    val quantity: Long,
) {
    fun toDomain() = Stock(ProductId(productId), quantity)
}

fun Stock.toDocument() = StockDocument(
    productId = productId.id,
    quantity = quantity
)
