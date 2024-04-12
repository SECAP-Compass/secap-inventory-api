package org.secapcompass.secapinventoryapi.configuration

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBClientBase
import com.eventstore.dbclient.EventStoreDBClientSettings
import com.eventstore.dbclient.EventStoreDBConnectionString
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.eventstore.dbclient.EventStoreDBProjectionManagementClient
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EventStoreDBConfiguration {

    @Value("\${eventstoredb.connectionString}")
    lateinit var connectionString: String

    val logger = LoggerFactory.getLogger(EventStoreDBConfiguration::class.java)


    fun eventStoreSettings(): EventStoreDBClientSettings {
        logger.info("EventStore connection string: $connectionString")
        return EventStoreDBConnectionString.parseOrThrow(connectionString)
    }

    @Bean
    fun eventStoreDBClient(): EventStoreDBClient {
        logger.info("Creating EventStoreDBClient")
        return EventStoreDBClient.create(eventStoreSettings())
    }

    @Bean
    fun eventStorePersistentSubscriptionsClient(): EventStoreDBPersistentSubscriptionsClient {
        return EventStoreDBPersistentSubscriptionsClient.from(eventStoreDBClient())
    }

    @Bean
    fun eventStoreProjectionManagementClient(): EventStoreDBProjectionManagementClient {
        return EventStoreDBProjectionManagementClient.from(eventStoreDBClient())
    }

    @Bean
    fun gsonMapper(): Gson {
        return Gson()
    }
}