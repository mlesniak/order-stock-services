package com.mlesniak.order.service

import com.mlesniak.order.client.ClientStock
import com.mlesniak.order.client.ReservationRequest
import com.mlesniak.order.client.StockServiceClient
import com.mlesniak.order.repository.OrderDocument
import com.mlesniak.order.repository.OrderRepository
import com.mlesniak.order.repository.toOrder
import com.mlesniak.order.service.exception.OrderNotSavedException
import com.mlesniak.order.service.model.OrderId
import com.mlesniak.order.service.model.ProductOrder
import com.mlesniak.order.service.model.toOrderId
import feign.FeignException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

/**
 * This service handles creation orders by reserving the respective quantities
 * for each product in the stock service. If the reservation fails, no order
 * is created. If it succeeds, the order is persisted and the order id is
 * returned for further processing (or retrieval via the corresponding get
 * functionality).
 */
// REMARK Note that we did not implement any form of verification of
//        ownership of an order nor other CRUD operations; see the
//        accompanying stock service for examples of these operations.
@Service
class OrderService(
    private val stockServiceClient: StockServiceClient,
    private var orderRepository: OrderRepository
) {
    private val log = KotlinLogging.logger {}

    // REMARK We would model different error cases once we begin to have
    //        different error cases. For now, we just have one (insufficient
    //        stock), therefore we do not need to complicate things.
    /**
     * Create a new order for the given products and their quantities.
     *
     * If the reservation of the order fails, null is returned. Otherwise,
     * the order id is returned.
     *
     * @param orders The list of products and their quantities.
     * @return The order id or null if the reservation failed.
     */
    fun create(orders: List<ProductOrder>): OrderId? {
        log.info { "Starting order process" }
        orders.forEach {
            log.debug { "Ordering $it" }
        }

        val reservationSuccessful = reserveProducts(orders)
        if (!reservationSuccessful) {
            return null
        }

        // Create and persist order.
        return saveOrder(orders)
    }

    private fun saveOrder(orders: List<ProductOrder>): OrderId {
        val orderDocument = OrderDocument(
            orders = orders.map { it.toOrder() }
        )
        val savedOrder = orderRepository.save(orderDocument)
        return savedOrder.id?.toOrderId() ?: throw OrderNotSavedException()
    }

    private fun reserveProducts(orders: List<ProductOrder>): Boolean {
        // Call stock service to reserve products.
        val reservationRequest = ReservationRequest(
            reservations = orders.map { order ->
                ClientStock(
                    productId = order.productId,
                    quantity = order.quantity
                )
            }
        )

        // REMARK Configuring Feign client to properly handle 422
        //        responses is out of scope, for the time being
        //        this is sufficient.
        try {
            stockServiceClient.reserveProducts(reservationRequest)
        } catch (e: FeignException) {
            log.info { "Reservation failed" }
            return false
        }

        log.info { "Reservation succeeded" }
        return true
    }

    /**
     * Retrieve the order with the given id.
     *
     * @param orderId The order id.
     * @return The order or null if no such order exists.
     */
    fun get(orderId: OrderId): List<ProductOrder>? {
        return orderRepository.findById(ObjectId(orderId.id))
            .map { orderDocument ->
                orderDocument.orders.map { it.toProductOrder() }
            }
            .orElse(null)
    }

    // REMARK Given more time, I would start a more thorough investigation
    //        and look for a fix since it pollutes the API of the service.

    // There seems to be a strange bug with MockK and FeignClients which
    // lead to MockK not finding an existing FeignClient. Therefore, we
    // do not simply use the usual @InjectMock annotation but have to use
    // this workaround.
    fun replaceRepository(orderRepository: OrderRepository) {
        this.orderRepository = orderRepository
    }
}
