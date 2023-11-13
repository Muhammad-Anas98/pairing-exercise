package io.billie.orders.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID
import javax.validation.constraints.NotBlank

data class ShipmentNotificationRequest(
        @field:NotBlank @JsonProperty("order_id") val orderId: UUID,
        @field:NotBlank @JsonProperty("organization_id") val entityId: UUID,
        @JsonProperty("shipped_items") val shippedItems: List<ShippedItem>,
        @field:NotBlank @JsonProperty("shipped_amount") val shipmentAmount: Double
)