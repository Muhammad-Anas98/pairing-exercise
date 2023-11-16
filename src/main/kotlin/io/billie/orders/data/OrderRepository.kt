package io.billie.orders.data

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.billie.orders.model.Item
import io.billie.orders.model.OrderResponse
import io.billie.orders.model.Shipment
import io.billie.orders.model.ShippedItem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Date
import java.sql.ResultSet
import java.sql.Timestamp
import java.sql.Types
import java.util.*

@Repository
class OrderRepository {
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Transactional(readOnly = true)
    fun findOrderById(id: UUID): OrderResponse? {
        return jdbcTemplate.query(orderQuery(id), orderMapper()).firstOrNull()

    }

    @Transactional
    fun update(order: OrderResponse, shipment: Shipment) {
        createShipment(shipment)
        updateOrder(order)
    }

    fun updateOrder(order: OrderResponse) {
        jdbcTemplate.update(
                "update organisations_schema.orders set shipped = ? where id = ?",
                order.shipped,
                order.id
        )
    }

    fun createShipment(shipment: Shipment) {
        val sql = """
        INSERT INTO organisations_schema.shipments
        (shipment_date, organization_id, shipped_items, shipment_amount, order_id)
        VALUES (?, ?, cast(? as jsonb), ?, ?)
    """.trimIndent()

        val parameters = arrayOf(
                Timestamp.valueOf(shipment.shipmentDate.atStartOfDay()),
                shipment.organizationId,
                objectMapper.writeValueAsString(shipment.shippedItems),
                shipment.shipmentAmount,
                shipment.orderId
        )

        jdbcTemplate.update(sql, parameters, intArrayOf(Types.TIMESTAMP, Types.OTHER, Types.OTHER, Types.DOUBLE, Types.OTHER))
    }

    fun getShipments(id: UUID): MutableList<Shipment>? {
        return jdbcTemplate.query(
                "select s.id, s.shipment_date, s.organization_id, s.shipped_items, s.shipment_amount, s.order_id " +
                        " from organisations_schema.shipments s where order_id = ?",
                shipmentMapper(),
                id
        )
    }

    @Transactional
    fun createOrder(order: OrderResponse): UUID {
        val keyHolder: KeyHolder = GeneratedKeyHolder()
        jdbcTemplate.update(
                { connection ->
                    val ps = connection.prepareStatement(
                            "INSERT INTO organisations_schema.orders (" +
                                    "total_amount, " +
                                    "shipped" +
                                    ") VALUES (?, ?)",
                            arrayOf("id")
                    )
                    ps.setDouble(1, order.totalAmount)
                    ps.setBoolean(2, order.shipped)
                    ps
                }, keyHolder
        )
        return keyHolder.getKeyAs(UUID::class.java)!!
    }

    private fun orderQuery(id: UUID) = "SELECT " +
            "o.id , " +
            "o.total_amount, " +
            "o.shipped, " +
            "i.id as item_id, " +
            "i.item_name, " +
            "i.quantity, " +
            "i.price " +
            " FROM " +
            "organisations_schema.orders o " +
            "LEFT JOIN organisations_schema.order_items oi ON oi.order_id::uuid = o.id::uuid " +
            "LEFT JOIN organisations_schema.items i ON oi.item_id::uuid = i.id::uuid " +
            "WHERE " +
            "o.id = '" + id + "'"

    private fun orderMapper() = RowMapper<OrderResponse> { it: ResultSet, _: Int ->
        val orderId = it.getObject("id", UUID::class.java)
        val totalAmount = it.getDouble("total_amount")
        val shipped = it.getBoolean("shipped")
        val items = mapItems(it)

        OrderResponse(orderId, items, totalAmount, shipped)
    }

    private fun mapItems(it: ResultSet): List<Item> {
        val items = mutableListOf<Item>()
        do {
            items.add(Item(
                    UUID.fromString(it.getString("item_id")),
                    it.getString("item_name"),
                    it.getInt("quantity"),
                    it.getDouble("price")
            ))
        } while (it.next())

        return items
    }

    private fun shipmentMapper() = RowMapper<Shipment> { it: ResultSet, _: Int ->
        Shipment(
                UUID.fromString(it.getString("id")),
                Date(it.getDate("shipment_date").time).toLocalDate(),
                UUID.fromString(it.getString("organization_id")),
                mapShippedItems(it),
                it.getDouble("shipment_amount"),
                UUID.fromString(it.getString("order_id"))
        )
    }

    private fun mapShippedItems(it: ResultSet): MutableList<ShippedItem> {
        val shippedItems = mutableListOf<ShippedItem>()

        val jsonbData = it.getString("shipped_items")
        val jsonArray = ObjectMapper().readTree(jsonbData) as JsonNode

        for (jsonNode in jsonArray) {
            val item = ShippedItem(
                    UUID.fromString(jsonNode["item_id"].asText()),
                    jsonNode["quantity"].asInt()
            )
            shippedItems.add(item)
        }
        return shippedItems
    }

}