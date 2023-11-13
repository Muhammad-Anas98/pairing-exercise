package io.billie.orders.data

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
@Repository
class ShipmentRepository {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate
}