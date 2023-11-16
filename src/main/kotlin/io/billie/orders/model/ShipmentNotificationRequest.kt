package io.billie.orders.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class ShipmentNotificationRequest(
        @field:NotBlank @JsonProperty("order_id") val orderId: String,
        @field:NotBlank @JsonProperty("organisation_id") val entityId: String,
        @JsonProperty("shipped_items") val shippedItems: List<ShippedItem>?,
        @field:NotNull @JsonProperty("shipped_amount") val shipmentAmount: Double
)