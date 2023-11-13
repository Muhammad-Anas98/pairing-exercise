package io.billie.orders.data

import io.billie.orders.model.Item
import io.billie.orders.model.OrderResponse
import io.billie.orders.model.Shipment
import io.billie.orders.model.ShippedItem
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Date
import java.sql.ResultSet
import java.util.*

@Repository
class OrderRepository {
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Transactional(readOnly = true)
    fun findOrderById(id: UUID): OrderResponse? {
        return jdbcTemplate.queryForObject(orderQuery(id), orderMapper())
    }

    @Transactional
    fun updateOrder(order: OrderResponse) {
        jdbcTemplate.update(
                "update organisations_schema.orders shipped = ? where id = ?",
                order.orderItems,
                order.totalAmount,
                order.shipped,
                order.id
        )
    }

    private fun orderQuery(id: UUID) = "SELECT" +
            "o.id , " +
            "o.total_amount, " +
            "o.shipped, " +
            "i.id as item_id, " +
            "i.item_name, " +
            "i.quantity " +
            "i.price, " +
            "s.id as shipment_id, " +
            "s.shipment_date, " +
            "s.organization_id, " +
            "s.shipment_amount, " +
            "s.shipped_items, " +
            " FROM " +
            "organisations_schema.orders o " +
            "INNER JOIN organisations_schema.order_items oi ON oi.order_id::uuid = o.id::uuid " +
            "INNER JOIN organisations_schema.items i ON oi.item_id::uuid = i.id::uuid " +
            "INNER JOIN organisations_schema.shipments s ON o.id::uuid = s.order_id::uuid " +
            "WHERE " +
            "o.id = "+ id

    private fun orderMapper() = RowMapper<OrderResponse> { it: ResultSet, _: Int ->
        OrderResponse(
                it.getObject("id", UUID::class.java),
                mapItems(it),
                it.getDouble("total_amount"),
                it.getBoolean("shipped"),
                mapShipments(it)
        )
    }

    private fun mapItems(it: ResultSet): List<Item> {
        val items = mutableListOf<Item>();
        while (it.next()) {
            items.add(Item(
                    UUID.fromString(it.getString("item_id")),
                    it.getString("item_name"),
                    it.getInt("quantity"),
                    it.getDouble("price")))
        }
        return items;
    }

    private fun mapShipments(it: ResultSet): MutableList<Shipment> {
        val shipments = mutableListOf<Shipment>();
        while (it.next()) {
            shipments.add(Shipment(
                    UUID.fromString(it.getString("shipment_id")),
                    Date(it.getDate("shipment_date").time).toLocalDate(),
                    UUID.fromString(it.getString("organization_id")),
                    mapShippedItems(it),
                    it.getDouble("shipment_amount"),
                    UUID.fromString(it.getString("order_id"))
            ))
        }
        return shipments;
    }

    private fun mapShippedItems(it: ResultSet): MutableList<ShippedItem> {
        val shippedItems = mutableListOf<ShippedItem>();
        while (it.next()) {
            shippedItems.add(ShippedItem(
                    UUID.fromString(it.getString("item_id")),
                    it.getInt("quantity")
            ))
        }
        return shippedItems;
    }

}