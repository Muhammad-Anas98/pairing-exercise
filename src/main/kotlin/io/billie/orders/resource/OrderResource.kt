package io.billie.orders.resource

import io.billie.orders.data.ShipmentNotificationException
import io.billie.orders.model.ShipmentNotificationRequest
import io.billie.orders.service.OrderService
import io.billie.organisations.data.UnableToFindCountry
import io.billie.organisations.viewmodel.*
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*
import javax.validation.Valid


@RestController
@RequestMapping("orders")
class OrderResource(val service: OrderService) {

    @PostMapping("/notify-shipment")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun post(@Valid @RequestBody shipmentNotification: ShipmentNotificationRequest) {
        try {
            service.processShipmentNotification(shipmentNotification)
        } catch (e: ShipmentNotificationException) {
            throw ResponseStatusException(BAD_REQUEST, e.message)
        }
    }

}
