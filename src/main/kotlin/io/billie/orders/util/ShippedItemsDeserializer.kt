package io.billie.orders.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import io.billie.orders.model.ShippedItem
import java.io.IOException
import java.util.*

class ShippedItemsDeserializer : JsonDeserializer<List<ShippedItem>>() {
    @Throws(IOException::class)
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): List<ShippedItem> {
        val codec = p.codec
        val node = codec.readTree<com.fasterxml.jackson.databind.JsonNode>(p)
        val items = mutableListOf<ShippedItem>()

        for (itemNode in node) {
            val itemId = UUID.fromString(itemNode.get("itemId").asText())
            val quantity = itemNode.get("quantity").asInt()
            items.add(ShippedItem(itemId, quantity))
        }

        return items
    }
}
