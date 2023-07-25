package com.mlesniak.order.service.exception

import com.mlesniak.order.service.model.ProductId

class StockAlreadyExistsException(productId: ProductId) : RuntimeException("Stock already exists for productId $productId")