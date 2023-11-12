package io.billie.orders.model

import java.util.UUID

data class Item(
        val itemId: UUID,
        val itemName: String,
        val quantity: Int,
        val price: Double,
)