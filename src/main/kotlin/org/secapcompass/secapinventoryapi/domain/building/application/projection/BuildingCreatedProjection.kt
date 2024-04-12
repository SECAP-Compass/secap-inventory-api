package org.secapcompass.secapinventoryapi.domain.building.application.projection

import com.eventstore.dbclient.CreatePersistentSubscriptionToAllOptions
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.eventstore.dbclient.PersistentSubscription
import com.eventstore.dbclient.PersistentSubscriptionListener
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.SubscriptionFilter
import com.google.gson.Gson
import kotlinx.serialization.json.Json
import org.secapcompass.secapinventoryapi.domain.building.core.event.BuildingCreatedEvent
import org.secapcompass.secapinventoryapi.domain.building.core.model.Building
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingPsqlRepo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.*

@Component
class BuildingCreatedProjection(
    eventStoreDBPersistentSubscriptionsClient: EventStoreDBPersistentSubscriptionsClient,
    private val buildingPsqlRepository: IBuildingPsqlRepo,
    private val gsonMapper: Gson
    ) {

    private companion object {
        const val CONSUMER_GROUP = "2building.created.consumer.group"
        const val EVENT_TYPE_PREFIX = "building.created"
    }

    private val logger: Logger = LoggerFactory.getLogger(BuildingCreatedProjection::class.java)

    init {
        val filter = SubscriptionFilter.newBuilder().addEventTypePrefix(EVENT_TYPE_PREFIX).build()

        val opts = CreatePersistentSubscriptionToAllOptions.get()
            .fromStart()
            .filter(filter)

        eventStoreDBPersistentSubscriptionsClient
            .createToAll(CONSUMER_GROUP, opts)

        val listener = BuildingCreatedListener()

        eventStoreDBPersistentSubscriptionsClient.subscribeToAll(
            CONSUMER_GROUP, listener
        )

        logger.info("Subscribed to all events with prefix: $EVENT_TYPE_PREFIX")
    }

    inner class BuildingCreatedListener : PersistentSubscriptionListener() {
        override fun onEvent(subscription: PersistentSubscription, retryCount: Int, event: ResolvedEvent) {
            logger.info("Event received: ${event.event.eventType}. AggregateId: ${event.event.streamId}, EventId: ${event.event.eventId}")

            val e = gsonMapper.fromJson(event.originalEvent.eventData.decodeToString(), BuildingCreatedEvent::class.java)
            logger.info("Event data: $event")

            val building = Building(id = UUID.fromString(event.event.streamId), address = e.address, area = 0.0)

            buildingPsqlRepository.saveBuilding(building)
        }

        override fun onCancelled(subscription: PersistentSubscription?, exception: Throwable?) {
            if (exception == null) {
                logger.info("Subscription is cancelled")
                return
            }

            logger.info("Subscription is dropped due to an error", exception)
        }
    }
}