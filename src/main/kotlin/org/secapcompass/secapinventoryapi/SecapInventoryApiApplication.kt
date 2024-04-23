package org.secapcompass.secapinventoryapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.CrossOrigin

@SpringBootApplication
class SecapInventoryApiApplication

fun main(args: Array<String>) {
    runApplication<SecapInventoryApiApplication>(*args)
}
