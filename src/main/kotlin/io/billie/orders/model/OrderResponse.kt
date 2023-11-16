package io.billie.orders.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID
data class OrderResponse(
        val id: UUID,
        @JsonProperty("order_items")
        var orderItems: List<Item>,
        @JsonProperty("total_amount")
        val totalAmount: Double,
        var shipped: Boolean = false,
        var shipments: MutableList<Shipment> = mutableListOf()
)