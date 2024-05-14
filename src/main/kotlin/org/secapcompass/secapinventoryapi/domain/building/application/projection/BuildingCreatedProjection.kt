package org.secapcompass.secapinventoryapi.domain.building.application.projection

import com.eventstore.dbclient.CreatePersistentSubscriptionToAllOptions
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.eventstore.dbclient.NackAction
import com.eventstore.dbclient.PersistentSubscription
import com.eventstore.dbclient.PersistentSubscriptionListener
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.SubscriptionFilter
import com.google.gson.Gson
import org.secapcompass.secapinventoryapi.configuration.ApplicationConfiguration
import org.secapcompass.secapinventoryapi.domain.building.core.event.BuildingCreatedEvent
import org.secapcompass.secapinventoryapi.domain.building.core.event.toAddress
import org.secapcompass.secapinventoryapi.domain.building.core.model.Building
import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingType
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingRepository
import org.secapcompass.secapinventoryapi.shared.eventsourcing.EventMetadata
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class BuildingCreatedProjection(
    eventStoreDBPersistentSubscriptionsClient: EventStoreDBPersistentSubscriptionsClient,
    private val buildingPsqlRepository: IBuildingRepository,
    private val gsonMapper: Gson,
    applicationConfiguration: ApplicationConfiguration
) {
    private companion object {
        const val EVENT_TYPE_PREFIX = "building.created"
    }

    private val logger: Logger = LoggerFactory.getLogger(BuildingCreatedProjection::class.java)

    init {
        val filter = SubscriptionFilter.newBuilder().addEventTypePrefix(EVENT_TYPE_PREFIX).build()

        val opts =
            CreatePersistentSubscriptionToAllOptions.get()
                .fromStart()
                .filter(filter)

        try {
            eventStoreDBPersistentSubscriptionsClient
                .createToAll(applicationConfiguration.BUILDING_CREATED_CONSUMER_GROUP, opts).join()
        } catch (e: Exception) {
            if (!e.message!!.contains("ALREADY_EXISTS")) {
                logger.error("Error creating subscription: ${e.message}")
                throw e
            }
        }

        val listener = BuildingCreatedListener()

        eventStoreDBPersistentSubscriptionsClient.subscribeToAll(
            applicationConfiguration.BUILDING_CREATED_CONSUMER_GROUP,
            listener,
        )

        logger.info("Subscribed to all events with prefix: $EVENT_TYPE_PREFIX")
    }

    inner class BuildingCreatedListener : PersistentSubscriptionListener() {
        override fun onEvent(
            subscription: PersistentSubscription,
            retryCount: Int,
            event: ResolvedEvent,
        ) {
            logger.info("Event received: ${event.event.eventType}. AggregateId: ${event.event.streamId}, EventId: ${event.event.eventId}")
            val e: BuildingCreatedEvent
            val emd: EventMetadata
            try {
                e = gsonMapper.fromJson(event.originalEvent.eventData.decodeToString(), BuildingCreatedEvent::class.java)
                emd = gsonMapper.fromJson(event.originalEvent.userMetadata.decodeToString(), EventMetadata::class.java)
            } catch (ex: Exception) {
                logger.error("Error while deserializing event data", ex)
                subscription.nack(NackAction.Park, "failed to deserialize event data")
                return
            }

            try {
                val type = BuildingType.valueOf(e.type)
                val building = Building(id = UUID.fromString(event.event.streamId), address = e.address.toAddress(), area = e.area, type = type, createdBy = emd.occurredBy)
                buildingPsqlRepository.saveBuilding(building)
                subscription.ack(event)
            }
            catch (ex: Exception) {
                logger.error("Error while creating building", ex)
                subscription.nack(NackAction.Park, "failed to create building")
                return
            }

        }

        override fun onCancelled(
            subscription: PersistentSubscription?,
            exception: Throwable?,
        ) {
            if (exception == null) {
                logger.info("Subscription is cancelled")
                return
            }

            logger.info("Subscription is dropped due to an error", exception)
        }
    }
}
