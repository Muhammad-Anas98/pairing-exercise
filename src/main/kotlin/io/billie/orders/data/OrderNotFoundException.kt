package io.billie.orders.data

import java.util.UUID

class OrderNotFoundException(val orderId: UUID) : RuntimeException()