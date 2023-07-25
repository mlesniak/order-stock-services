package com.mlesniak.order.service.exception

import com.mlesniak.order.service.model.Stock

class InsufficientStocksException(val availableStocks: List<Stock>) : RuntimeException()