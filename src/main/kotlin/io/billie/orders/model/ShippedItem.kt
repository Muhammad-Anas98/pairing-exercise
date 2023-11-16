package io.billie.orders.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class ShippedItem(
        @JsonProperty("item_id")
        val itemId: UUID,
        val quantity: Int
)