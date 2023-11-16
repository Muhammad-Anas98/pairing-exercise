package io.billie.orders.model

import ShippedItemsSerializer
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.billie.orders.util.ShippedItemsDeserializer
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.util.*
data class Shipment(
        val id: UUID,
        @JsonFormat(pattern = "dd/MM/yyyy")
        @JsonProperty("shipment_date")
        val shipmentDate: LocalDate,
        @JsonProperty("organization_id")
        val organizationId: UUID,
        @JsonDeserialize(using = ShippedItemsDeserializer::class)
        @JsonSerialize(using = ShippedItemsSerializer::class)
        @JsonProperty("shipped_items")
        val shippedItems: MutableList<ShippedItem> = mutableListOf(),
        @JsonProperty("shipment_amount")
        val shipmentAmount: Double,
        @JsonProperty("order_id")
        val orderId : UUID
)