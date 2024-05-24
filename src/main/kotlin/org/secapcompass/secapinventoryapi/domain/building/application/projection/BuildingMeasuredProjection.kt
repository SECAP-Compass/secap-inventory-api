package org.secapcompass.secapinventoryapi.domain.building.application.projection

import com.eventstore.dbclient.CreatePersistentSubscriptionToAllOptions
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.eventstore.dbclient.NackAction
import com.eventstore.dbclient.PersistentSubscription
import com.eventstore.dbclient.PersistentSubscriptionListener
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.SubscriptionFilter
import com.google.gson.Gson
import kotlinx.serialization.json.Json
import org.secapcompass.secapinventoryapi.configuration.ApplicationConfiguration
import org.secapcompass.secapinventoryapi.domain.building.core.event.BuildingMeasuredEvent
import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingMeasurement
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingMeasurementRepository
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingRepository
import org.secapcompass.secapinventoryapi.shared.eventsourcing.EventMetadata
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.jvm.optionals.getOrElse


// We will not listen building.measured
// We will listen building.measurement.calculated
// But for this bounded context, we can call it building measurements.
@Component
class BuildingMeasuredProjection(
    private val buildingMeasurementRepository: IBuildingMeasurementRepository,
    private val buildingRepository: IBuildingRepository,
    private val gsonMapper: Gson,
    eventStoreDBPersistentSubscriptionsClient: EventStoreDBPersistentSubscriptionsClient,
    applicationConfiguration: ApplicationConfiguration
) {

    private val logger = LoggerFactory.getLogger(BuildingMeasuredProjection::class.java)

    init {
        val EVENT_TYPE_PREFIX = "building.measurement.calculated"
        val filter = SubscriptionFilter.newBuilder().addEventTypePrefix(EVENT_TYPE_PREFIX).build()

        val opts =
            CreatePersistentSubscriptionToAllOptions.get()
                .fromStart()
                .filter(filter)

        try {
            eventStoreDBPersistentSubscriptionsClient
                .createToAll(applicationConfiguration.BUILDING_MEASURED_CONSUMER_GROUP, opts).join()
        }
        catch (e: Exception) {
            if (!e.message!!.contains("ALREADY_EXISTS")) {
                logger.error("Error creating subscription: ${e.message}")
                throw e
            }
        }

        val listener = BuildingMeasuredListener()

        eventStoreDBPersistentSubscriptionsClient.subscribeToAll(
            applicationConfiguration.BUILDING_MEASURED_CONSUMER_GROUP,
            listener,
        )

        logger.info("Subscribed to all events")
    }


    // Here, we may use a coroutine pool?
    inner class BuildingMeasuredListener : PersistentSubscriptionListener() {
        override fun onEvent(
            subscription: PersistentSubscription,
            retryCount: Int,
            event: ResolvedEvent,
        ) {
            logger.info("Event received: ${event.event.eventType}. AggregateId: ${event.event.streamId}, EventId: ${event.event.eventId}")

            buildingRepository.getBuildingById(UUID.fromString(event.event.streamId))
                .getOrElse { subscription.nack(NackAction.Park, "building not found") }

            val buildingMeasuredEvent: BuildingMeasuredEvent
            val emd: EventMetadata
            try {
                buildingMeasuredEvent = gsonMapper.fromJson(event.event.eventData.decodeToString(), BuildingMeasuredEvent::class.java)
                emd = gsonMapper.fromJson(event.event.userMetadata.decodeToString(), EventMetadata::class.java)
            } catch (e: Exception) {
                logger.error("Error deserializing event data: ${e.message}")
                subscription.nack(NackAction.Park, "failed to deserialize event data")
                return
            }

            val buildingMeasurement = BuildingMeasurement(
                event.event.eventId,
                UUID.fromString(event.event.streamId),
                buildingMeasuredEvent.measurement,
                emd.occurredBy
            )
            buildingMeasurementRepository.saveBuildingMeasurement(buildingMeasurement)
            subscription.ack(event)
        }
    }
}