package org.secapcompass.secapinventoryapi.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {

    @Value("\${eventstoredb.consumer-groups.building-measured-consumer-group}")
    lateinit var BUILDING_MEASURED_CONSUMER_GROUP: String

    @Value("\${eventstoredb.consumer-groups.building-created-consumer-group}")
    lateinit var BUILDING_CREATED_CONSUMER_GROUP: String
}