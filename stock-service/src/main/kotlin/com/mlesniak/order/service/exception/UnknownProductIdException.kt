package com.mlesniak.order.service.exception

import com.mlesniak.order.service.model.ProductId

class UnknownProductIdException(productId: ProductId) : RuntimeException("Unknown productId $productId")