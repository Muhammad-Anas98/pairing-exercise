package io.billie.orders.model
import java.time.LocalDate
import java.util.*

data class Shipment(
        val shipmentId: UUID,
        val shipmentDate: LocalDate,
        val organizationId: UUID,
        val shippedItems: MutableList<ShippedItem> = mutableListOf(),
        val shipmentAmount: Double
)