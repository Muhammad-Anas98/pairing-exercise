package io.billie.orders.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class OrderResponse(
        val id: UUID,
        val items: List<Item>,
        @JsonProperty("total_amount") val totalAmount: Double,
        var shipped: Boolean = false,
        val shipments: MutableList<Shipment> = mutableListOf()
)