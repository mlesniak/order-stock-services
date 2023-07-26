package com.mlesniak.order

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@EnableFeignClients
@EnableMongoRepositories
class OrderApplication : CommandLineRunner {
    private val log = KotlinLogging.logger {}

    override fun run(vararg args: String?) {
        log.info { "Starting order service" }
    }
}

fun main(args: Array<String>) {
    runApplication<OrderApplication>(*args)
}
