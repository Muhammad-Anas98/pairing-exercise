package io.billie.functional


import com.fasterxml.jackson.databind.ObjectMapper
import io.billie.functional.data.Fixtures
import io.billie.functional.data.Fixtures.existedDummyOrderId
import io.billie.functional.data.Fixtures.notExistedDummyOrderId
import io.billie.functional.data.Fixtures.notExistedDummyOrgId
import io.billie.functional.data.Fixtures.shipmentRequestWithEmptyShippedItems
import io.billie.organisations.viewmodel.Entity
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = DEFINED_PORT)
class CanProcessShipmentNotificationTest {

    @LocalServerPort
    private val port = 8080

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mapper: ObjectMapper

    @Autowired
    private lateinit var template: JdbcTemplate


    @Test
    fun cannotProcessShipmentNotification_WhenOrganisationIsNotRegistered() {
        val shipmentRequest = shipmentRequestWithEmptyShippedItems(existedDummyOrderId(), notExistedDummyOrgId(), 100.0)

        mockMvc.perform(
                post("/orders/notify-shipment").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(shipmentRequest))
        )
                .andExpect(status().isBadRequest())
    }

    @Test
    fun cannotProcessShipmentNotification_WhenOrderDoesNotExists() {
        val shipmentRequest = shipmentRequestWithEmptyShippedItems(notExistedDummyOrderId(), createOrganisation(), 100.0)

        mockMvc.perform(
                post("/orders/notify-shipment").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(shipmentRequest))
        )
                .andExpect(status().isBadRequest())
    }

    @Test
    fun cannotProcessShipmentNotification_WhenShipmentAmountExceedsTotalAmount() {
        val shipmentRequest = shipmentRequestWithEmptyShippedItems(existedDummyOrderId(), createOrganisation(), 200.0)

        mockMvc.perform(
                post("/orders/notify-shipment").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(shipmentRequest))
        )
                .andExpect(status().isBadRequest())
    }

    //To rerun this test as end to end test please rebuild the image again as db might have old dummy data and can produce false results
    @Test
    fun canProcessShipmentNotification() {
        val shipmentRequest = shipmentRequestWithEmptyShippedItems(existedDummyOrderId(), createOrganisation(), 100.0)

        mockMvc.perform(
                post("/orders/notify-shipment").contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(shipmentRequest))
        )
                .andExpect(status().isOk())
        val org: Map<String, Any> = orgFromDatabase(existedDummyOrderId())

        assert(org["shipped"] == true)

    }

    private fun createOrganisation() : UUID {
        val result = mockMvc.perform(
                post("/organisations").contentType(APPLICATION_JSON).content(Fixtures.orgRequestJson())
        )
                .andExpect(status().isOk)
                .andReturn()

        val response = mapper.readValue(result.response.contentAsString, Entity::class.java)
        return response.id
    }

    private fun queryEntityFromDatabase(sql: String, id: UUID): MutableMap<String, Any> =
            template.queryForMap(sql, id)

    private fun orgFromDatabase(id: UUID): MutableMap<String, Any> =
            queryEntityFromDatabase("select * from organisations_schema.orders where id = ?", id)

}