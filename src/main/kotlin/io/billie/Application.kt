package io.billie

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

@SpringBootApplication
class Application

fun main(args: Array<String>) {
//    runApplication<Application>(*args)
    System.out.print(UUID.randomUUID())
}
