// package com.mlesniak.order
//
// import org.springframework.boot.fromApplication
// import org.springframework.boot.test.context.TestConfiguration
// import org.springframework.boot.testcontainers.service.connection.ServiceConnection
// import org.springframework.boot.with
// import org.springframework.context.annotation.Bean
// import org.testcontainers.containers.MongoDBContainer
//
// @TestConfiguration(proxyBeanMethods = false)
// class TestStockServiceApplication {
//
// 	@Bean
// 	@ServiceConnection
// 	fun mongoDbContainer(): MongoDBContainer {
// 		return MongoDBContainer("mongo:latest")
// 	}
//
// }
//
// fun main(args: Array<String>) {
// 	fromApplication<StockApplication>().with(TestStockServiceApplication::class).run(*args)
// }
