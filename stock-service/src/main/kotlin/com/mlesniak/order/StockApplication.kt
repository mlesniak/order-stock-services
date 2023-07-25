package com.mlesniak.order

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@EnableMongoRepositories
class StockApplication : CommandLineRunner {
    private val log = KotlinLogging.logger {}

    override fun run(vararg args: String?) {
        log.info { "Starting stock service" }
    }
}

fun main(args: Array<String>) {
    runApplication<StockApplication>(*args)
}
