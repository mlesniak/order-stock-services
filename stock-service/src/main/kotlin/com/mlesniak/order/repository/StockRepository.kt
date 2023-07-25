package com.mlesniak.order.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface StockRepository : MongoRepository<StockDocument, ObjectId> {
    fun findByProductId(productId: Long): StockDocument?
    fun deleteByProductId(productId: Long)
}
