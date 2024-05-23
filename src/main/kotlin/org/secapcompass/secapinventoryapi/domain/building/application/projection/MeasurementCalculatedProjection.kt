package org.secapcompass.secapinventoryapi.domain.building.application.projection

import com.eventstore.dbclient.*
import com.google.gson.Gson
import org.secapcompass.secapinventoryapi.configuration.ApplicationConfiguration
import org.secapcompass.secapinventoryapi.domain.building.core.event.BuildingMeasurementCalculatedEvent
import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingMeasurementCalculation
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingMeasurementCalculationRepository
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingRepository
import org.secapcompass.secapinventoryapi.shared.eventsourcing.EventMetadata
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Component
class MeasurementCalculatedProjection(
    private val buildingMeasurementCalculationRepository:IBuildingMeasurementCalculationRepository,
    private val buildingRepository: IBuildingRepository,
    private val gsonMapper: Gson,
    eventStoreDBPersistentSubscriptionsClient: EventStoreDBPersistentSubscriptionsClient,
    applicationConfiguration: ApplicationConfiguration
) {

    private val logger = LoggerFactory.getLogger(BuildingMeasuredProjection::class.java)

    init {
        val EVENT_TYPE_PREFIX = "building.measurementCalculated"
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

        val listener = BuildingMeasurementCalculatedListener()

        eventStoreDBPersistentSubscriptionsClient.subscribeToAll(
            applicationConfiguration.BUILDING_MEASURED_CONSUMER_GROUP,
            listener,
        )

        logger.info("Subscribed to all events")
    }

    inner class BuildingMeasurementCalculatedListener : PersistentSubscriptionListener() {
        override fun onEvent(
            subscription: PersistentSubscription,
            retryCount: Int,
            event: ResolvedEvent,
        ) {
            logger.info("Event received: ${event.event.eventType}. AggregateId: ${event.event.streamId}, EventId: ${event.event.eventId}")

            buildingRepository.getBuildingById(UUID.fromString(event.event.streamId))
                .getOrElse { subscription.nack(NackAction.Park, "building not found") }

            val buildingMeasurementCalculatedEvent:BuildingMeasurementCalculatedEvent
            val emd: EventMetadata
            try {
                buildingMeasurementCalculatedEvent = gsonMapper.fromJson(event.event.eventData.decodeToString(), BuildingMeasurementCalculatedEvent::class.java)
                emd = gsonMapper.fromJson(event.event.userMetadata.decodeToString(), EventMetadata::class.java)
            } catch (e: Exception) {
                logger.error("Error deserializing event data: ${e.message}")
                subscription.nack(NackAction.Park, "failed to deserialize event data")
                return
            }

            val buildingMeasurementCalculation = BuildingMeasurementCalculation(
                event.event.eventId,
                UUID.fromString(event.event.streamId),
                buildingMeasurementCalculatedEvent.calculatedMeasurement,
                emd.occurredBy
            )
            buildingMeasurementCalculationRepository.saveBuildingMeasurementCalculation(buildingMeasurementCalculation)
            subscription.ack(event)
        }
    }
}