package io.billie.orders.model

import java.util.UUID

data class ShippedItem(
        val itemId: UUID,
        val quantity: Int
)