package com.mlesniak.order.controller

import com.mlesniak.order.controller.model.ClientStock
import com.mlesniak.order.controller.model.InsufficientStocksResponse
import com.mlesniak.order.controller.model.ReservationRequest
import com.mlesniak.order.controller.model.ResponseResult
import com.mlesniak.order.controller.model.RestResult
import com.mlesniak.order.controller.model.RestResult.Companion.badRequest
import com.mlesniak.order.controller.model.RestResult.Companion.ok
import com.mlesniak.order.controller.model.RestockRequest
import com.mlesniak.order.service.StockService
import com.mlesniak.order.service.exception.ConcurrentStockModificationException
import com.mlesniak.order.service.exception.InsufficientStocksException
import com.mlesniak.order.service.exception.UnknownProductIdException
import com.mlesniak.order.service.model.ProductId
import com.mlesniak.order.service.model.ProductId.Companion.toProductId
import com.mlesniak.order.service.model.Stock
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

// REMARK We are currently not logging request and responses If this is
//        necessary depends on the surrounding infrastructure, i.e. do
//        we have a sidecar, a proxy, ... which accompanies the service?
@RestController
class StockController(
    private val stockService: StockService
) {
    @GetMapping("/stock/{productId}")
    fun getStock(@PathVariable productId: Long): ResponseResult<ClientStock> {
        val stock = stockService.getQuantity(ProductId(productId))
            ?: return RestResult<ClientStock>(
                message = "Product with productId $productId not found",
                statusCode = HttpStatus.NOT_FOUND
            ).build()
        return ok(ClientStock(productId, stock))
    }

    @PostMapping("/stock/{productId}")
    fun create(@PathVariable productId: Long): ResponseResult<Unit> {
        stockService.create(productId.toProductId())
        return ok(statusCode = HttpStatus.CREATED)
    }

    @PutMapping("/stock/{productId}")
    fun restock(
        @PathVariable productId: Long,
        @RequestBody restockRequest: RestockRequest
    ): ResponseResult<Unit> {
        try {
            val stock = Stock(productId.toProductId(), restockRequest.quantity)
            stockService.restock(stock)
            return ok()
        } catch (e: UnknownProductIdException) {
            return RestResult<Unit>(
                message = "Product with productId $productId not found",
                statusCode = HttpStatus.NOT_FOUND
            ).build()
        } catch (e: ConcurrentStockModificationException) {
            return RestResult<Unit>(
                message = "Concurrent modification of stock for productId $productId",
                statusCode = HttpStatus.CONFLICT
            ).build()
        }
    }

    @DeleteMapping("/stock/{productId}")
    fun delete(@PathVariable productId: Long): ResponseResult<Unit> {
        // REMARK We do not check if the product exists,
        //        because we want to delete it anyway.
        stockService.remove(productId.toProductId())
        return ok()
    }

    @PostMapping("/stock/reserve")
    fun reserve(@RequestBody request: ReservationRequest): ResponseResult<InsufficientStocksResponse> {
        val orderedProducts = request.reservations.map(ClientStock::toStock)
        return try {
            stockService.reserve(orderedProducts)
            ok()
        } catch (e: InsufficientStocksException) {
            badRequest(
                data = InsufficientStocksResponse(e.availableStocks),
                message = "Insufficient stocks",
                // REMARK How specific should we be here? In the end, this depends
                //        on the existing conventions and if the client is able
                //        to handle the error codes accordingly.
                // The HyperText Transfer Protocol 422 Unprocessable Content
                // response status code indicates that the server understands the
                // content type of the request entity, and the syntax of the request
                // entity is correct, but it was unable to process the contained
                // instructions.
                statusCode = HttpStatus.UNPROCESSABLE_ENTITY
            )
        }
    }
}
