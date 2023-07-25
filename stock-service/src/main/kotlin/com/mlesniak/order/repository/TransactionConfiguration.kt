package com.mlesniak.order.repository

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration

@Configuration
class TransactionConfiguration : AbstractMongoClientConfiguration() {
    @Value("\${spring.data.mongodb.database:test}")
    private lateinit var databaseName: String

    @Value("\${spring.data.mongodb.uri}")
    private val uri: String? = null

    @Bean
    fun transactionManager(dbFactory: MongoDatabaseFactory): MongoTransactionManager =
        MongoTransactionManager(dbFactory)

    override fun mongoClient(): MongoClient =
        MongoClients.create(uri!!)

    override fun getDatabaseName(): String = databaseName
}