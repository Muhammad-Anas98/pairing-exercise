package io.billie.orders.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Item(
        val id: UUID,
        @JsonProperty("item_name")
        val itemName: String,
        val quantity: Int,
        val price: Double,
)