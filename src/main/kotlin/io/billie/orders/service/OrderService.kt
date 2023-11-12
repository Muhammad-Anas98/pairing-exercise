package io.billie.orders.service

import io.billie.orders.data.OrderRepository
import io.billie.orders.data.ShipmentNotificationException
import io.billie.orders.model.*
import io.billie.organisations.data.OrganisationRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class OrderService(val orgDb: OrganisationRepository, val orderDb: OrderRepository) {

    fun processShipmentNotification(request: ShipmentNotificationRequest) {
        val order = orderDb.findOrderById(request.orderId)
                ?: throw ShipmentNotificationException("Order does not exist for id: ${request.orderId}")

        if (order.shipped) {
            throw ShipmentNotificationException("Order is already shipped")
        }

        val organization = orgDb.findOrganisationById(request.organizationId)
                ?: throw ShipmentNotificationException("Organization does not exist for id: ${request.organizationId}")

        // Validate shipment amount by also considering previous shipments
        val shipmentsAmount = request.shipmentAmount + cumulatePreviousShipmentsAmount(order)
        if (shipmentsAmount > order.totalAmount) {
            throw ShipmentNotificationException("Shipment amount exceeds total order amount")
        }

        val shipment = Shipment(
                shipmentId = UUID.randomUUID(),
                shipmentDate = LocalDate.now(),
                organizationId = organization.id,
                shipmentAmount = request.shipmentAmount
        )

        // if shipped items are provided in the Request than Update the order and track shipped items
        for (shippedItemRequest in request.shippedItems) {
            val item = order.items.find { it.itemId == shippedItemRequest.itemId }
                    ?: throw ShipmentNotificationException("Item ${shippedItemRequest.itemId} does not exist in order ${order.id}")

            // Validate quantity by considering previously shipped item's quantity
            val shippedItemQuantity = shippedItemRequest.quantity + cumulatePreviousShippedItemQuantity(order.shipments, item.itemId)
            if (shippedItemQuantity > item.quantity) {
                throw ShipmentNotificationException("Invalid quantity for item ${item.itemId}")
            }

            // Track the shipped item
            shipment.shippedItems.add(ShippedItem(item.itemId, shippedItemRequest.quantity))
        }

        // Add the shipment to the order
        order.shipments.add(shipment)

        // Update the order status to shipped if the amount satisfies
        // (as shipped items is not required field in the request as per the requirement)
        order.shipped = shipmentsAmount == order.totalAmount

        orderDb.updateOrder(order)
    }

    fun cumulatePreviousShipmentsAmount(order: OrderResponse): Double {
        return order.shipments.sumOf { it.shipmentAmount }
    }

    fun cumulatePreviousShippedItemQuantity(previousShipments: MutableList<Shipment>, itemId: UUID): Int {
        return previousShipments.sumOf { it.shippedItems.find { it.itemId == itemId }?.quantity ?: 0 }
    }
}

