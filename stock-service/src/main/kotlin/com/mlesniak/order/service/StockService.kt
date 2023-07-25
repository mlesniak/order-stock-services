package com.mlesniak.order.service

import com.mlesniak.order.repository.StockRepository
import com.mlesniak.order.repository.toDocument
import com.mlesniak.order.service.exception.ConcurrentStockModificationException
import com.mlesniak.order.service.exception.InsufficientStocksException
import com.mlesniak.order.service.exception.StockAlreadyExistsException
import com.mlesniak.order.service.exception.UnknownProductIdException
import com.mlesniak.order.service.model.ProductId
import com.mlesniak.order.service.model.Stock
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * The central stock services which encapsulates all business logic.
 * In addition to basic CRUD operations, it also provides the ability to
 * reserve a set of products with their quantitys.
 *
 * Increasing and decreasing the quantity of a product is separated between
 * reserving (decreasing) and restocking (increasing).
 */
// REMARK In a (domain) service we try to use only domain objects (and not
//        technical ones) to prevent technical implementation leaking.
@Service
class StockService(
    val stockRepository: StockRepository
) {
    private val log = KotlinLogging.logger {}

    // REMARK We intentionally do not allow to create a product with a
    //        set quantity. Instead, we require the product to be created
    //        first and then restock it. Our main motivation is that
    //        setting up a product and restocking (even in the beginning)
    //        are (probably!) two different business processes.
    /**
     * Allows to create a new product with zero quantity.
     *
     * @param productId The id of the product to create.
     * @throws StockAlreadyExistsException If the product already exists.
     */
    fun create(productId: ProductId) {
        log.info { "Creating new product with productId=$productId" }
        if (stockRepository.findByProductId(productId.id) != null) {
            throw StockAlreadyExistsException(productId)
        }
        stockRepository.save(Stock(productId, 0).toDocument())
    }

    /**
     * Return the quantity of a product.
     *
     * @param productId The id of the product.
     * @return The quantity of the product or [null] if the product does not exist.
     */
    fun getQuantity(productId: ProductId): Long? {
        log.debug { "Getting quantity for $productId" }
        return stockRepository
            .findByProductId(productId.id)
            ?.let { it.toDomain().quantity }
    }

    // REMARK Currently this is a basic implementation. In future improvements,
    //        we could add a transaction id which is returned to the caller
    //        which could be used to cancel the reservation, for example.
    // REMARK Return [null] in case of success is arguable. We could also
    //        wrap it into a specific domain object, e.g. ReservationResult,
    //        but right now this would just overcomplicate things.
    /**
     * Reserve a set of products with their respective quantitys. If the
     * reservation is successful, the quantitys of the product are decreased,
     * if it fails, e.g. at least for one product the available quantity is
     * not sufficient, the reservation is rolled back. In this case we
     * also return a list of requested products with their current
     * available quantitys.
     *
     * @param reservations The list of products with their quantitys to reserve.
     * @return In case of insufficient availabilities, the list of products with
     *         their current available quantitys. In case of success, null.
     */
    @Transactional
    fun reserve(reservations: List<Stock>): List<Stock>? {
        // REMARK While we do not have an explicit identifier, the header
        //        value for X-Request-Id is implicitly used as a span
        //        identifier to group all log messages for this reservation.
        log.info { "Starting reservation" }
        val possibleReservations = mutableListOf<Stock>()
        var reservationFailed = false

        for (stockReservation in reservations) {
            val dbStock = stockRepository
                .findByProductId(stockReservation.productId.id)
                ?: throw UnknownProductIdException(stockReservation.productId)

            val stockQuantityLeft = dbStock.quantity - stockReservation.quantity
            if (stockQuantityLeft < 0) {
                log.info { "Not enough stock for $stockReservation. Requested ${stockReservation.quantity}, available ${dbStock.quantity}" }
                reservationFailed = true
            } else {
                log.info { "Reserving $stockReservation" }
            }
            possibleReservations.add(
                Stock(
                    ProductId(dbStock.productId),
                    dbStock.quantity
                )
            )

            val updatedStock = dbStock.copy(quantity = stockQuantityLeft)
            stockRepository.save(updatedStock)
        }

        if (reservationFailed) {
            log.info { "Reservation failed" }
            // REMARK We need to throw an exception here to force
            //        a rollback of the transaction. There are other
            //        ways to do this, but this is the most explicit
            //        and idiomatic one.
            throw InsufficientStocksException(possibleReservations)
        }

        log.info { "Reservation successfully finished" }
        return null
    }

    /**
     * Remove a product from the stock.
     *
     * Note that we do not throw an error if the product does not exist.
     *
     * @param productId The id of the product to remove.
     */
    fun remove(productId: ProductId) {
        log.info { "Removing stock for productId=$productId" }
        stockRepository.deleteByProductId(productId.id)
    }

    /**
     * Restock a product with a given quantity.
     *
     * @param stockUpdate The product and the quantity to restock.
     * @throws ConcurrentStockModificationException If the product was modified
     *         concurrently in the meantime.
     */
    fun restock(stockUpdate: Stock) {
        log.info { "Restocking $stockUpdate" }
        val dbStock = stockRepository
            .findByProductId(stockUpdate.productId.id)
            ?: throw UnknownProductIdException(stockUpdate.productId)
        val updatedStock = dbStock.copy(
            quantity = dbStock.quantity + stockUpdate.quantity
        )
        try {
            stockRepository.save(updatedStock)
        } catch (e: OptimisticLockingFailureException) {
            // REMARK Every communication with the domain is wrapped
            //        in a domain-specific object. In this case, we
            //        wrap the technical exception in a domain-specific
            //        one.
            log.warn { "Optimistic locking failure for $stockUpdate" }
            throw ConcurrentStockModificationException(stockUpdate.productId)
        }
    }
}