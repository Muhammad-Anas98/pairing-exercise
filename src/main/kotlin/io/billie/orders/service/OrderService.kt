package io.billie.orders.service

import io.billie.orders.data.OrderRepository
import io.billie.orders.data.ShipmentNotificationException
import io.billie.orders.model.*
import io.billie.organisations.data.OrganisationRepository
import io.billie.organisations.viewmodel.OrganisationResponse
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class OrderService(val orgDb: OrganisationRepository, val orderDb: OrderRepository) {

    @Async
    fun processShipmentNotification(request: ShipmentNotificationRequest) {
        val order = findAndValidateOrderForShipment(UUID.fromString(request.orderId))
        val organization = findAndValidateOrganization(UUID.fromString(request.entityId))

        validateShipmentsAmount(order, request)

        val shipment = createShipment(request, organization)
        populateAndValidateShippedItemsIfProvided(order, shipment, request)

        order.shipments.add(shipment)

        // Update the order status to shipped if the amount satisfies
        // (as shipped items is not required field in the request as per the requirement we can assume that items quantity satisfies)
        order.shipped = cumulatePreviousShipmentsAmount(order) == order.totalAmount
        orderDb.update(order, shipment)
    }

    private fun findAndValidateOrderForShipment(orderId: UUID): OrderResponse {
        val order = orderDb.findOrderById(orderId)
                ?: throw ShipmentNotificationException("Order does not exist for id: $orderId")

        if (order.shipped) {
            throw ShipmentNotificationException("Order is already shipped")
        }

        val shipments = orderDb.getShipments(orderId)
        if (shipments != null) {
            order.shipments = shipments
        }

        return order
    }

    private fun findAndValidateOrganization(organizationId: UUID): OrganisationResponse {
        return orgDb.findOrganisationById(organizationId)
                ?: throw ShipmentNotificationException("Organization does not exist for id: $organizationId")
    }

    private fun validateShipmentsAmount(order: OrderResponse, request: ShipmentNotificationRequest) {
        val shipmentsAmount = request.shipmentAmount + cumulatePreviousShipmentsAmount(order)
        if (shipmentsAmount > order.totalAmount) {
            throw ShipmentNotificationException("Shipment amount exceeds total order amount")
        }
    }

    private fun createShipment(request: ShipmentNotificationRequest, organization: OrganisationResponse): Shipment {
        val shipmentId = UUID.randomUUID()
        val shipmentDate = LocalDate.now()

        return Shipment(
                id = shipmentId,
                shipmentDate = shipmentDate,
                organizationId = organization.id,
                shipmentAmount = request.shipmentAmount,
                orderId = UUID.fromString(request.orderId)
        )
    }

    private fun populateAndValidateShippedItemsIfProvided(order: OrderResponse, shipment: Shipment, request: ShipmentNotificationRequest) {
        if (request.shippedItems.isNullOrEmpty() || order.shipments.isEmpty()) {
            return
        }

        for (shippedItemRequest in request.shippedItems) {
            val item = order.orderItems.find { it.id == shippedItemRequest.itemId }
                    ?: throw ShipmentNotificationException("Item ${shippedItemRequest.itemId} does not exist in order ${order.id}")

            // Validate quantity by considering previously shipped item's quantity
            val shippedItemQuantity = shippedItemRequest.quantity + cumulatePreviousShippedItemQuantity(order.shipments, item.id)
            if (shippedItemQuantity > item.quantity) {
                throw ShipmentNotificationException("Invalid quantity for item ${item.id}")
            }

            // Track the shipped item
            shipment.shippedItems.add(ShippedItem(item.id, shippedItemRequest.quantity))
        }

    }

    private fun cumulatePreviousShipmentsAmount(order: OrderResponse): Double {
        return order.shipments.sumOf { it.shipmentAmount }
    }

    private fun cumulatePreviousShippedItemQuantity(previousShipments: MutableList<Shipment>, itemId: UUID): Int {
        return previousShipments.sumOf { it.shippedItems.find { it.itemId == itemId }?.quantity ?: 0 }
    }
}

