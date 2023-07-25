package com.mlesniak.order.service.model

/**
 * Key pillar for a stock.
 */
// REMARK Encapsulate the id in a cheap class to avoid
//        confusion and mistakes with other (future) ids.
@JvmInline
value class ProductId(val id: Long) {
    override fun toString() = "$id"

    companion object {
        fun Long.toProductId() = ProductId(this)
    }
}
