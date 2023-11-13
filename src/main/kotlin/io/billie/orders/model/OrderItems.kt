package io.billie.orders.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class OrderItems (
        val id: UUID,
        @JsonProperty("order_id")
        val orderId: UUID,
        @JsonProperty("item_id")
        val itemId: UUID
)