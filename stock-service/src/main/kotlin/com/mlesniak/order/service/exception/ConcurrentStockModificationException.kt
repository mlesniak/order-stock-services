package com.mlesniak.order.service.exception

import com.mlesniak.order.service.model.ProductId

class ConcurrentStockModificationException(productId: ProductId) : RuntimeException("Operation failed for $productId due to concurrent modification")