import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import io.billie.orders.model.ShippedItem
import java.io.IOException

class ShippedItemsSerializer : JsonSerializer<List<ShippedItem>>() {
    @Throws(IOException::class)
    override fun serialize(value: List<ShippedItem>?, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeStartArray()

        value?.forEach { item ->
            gen.writeStartObject()
            gen.writeStringField("itemId", item.itemId.toString())
            gen.writeNumberField("quantity", item.quantity)
            gen.writeEndObject()
        }

        gen.writeEndArray()
    }
}
