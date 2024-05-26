package org.secapcompass.secapinventoryapi.domain.building.application.projection

import com.eventstore.dbclient.CreatePersistentSubscriptionToAllOptions
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.eventstore.dbclient.NackAction
import com.eventstore.dbclient.PersistentSubscription
import com.eventstore.dbclient.PersistentSubscriptionListener
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.SubscriptionFilter
import com.google.gson.Gson
import jakarta.persistence.LockModeType
import jakarta.transaction.Transactional
import org.secapcompass.secapinventoryapi.configuration.ApplicationConfiguration
import org.secapcompass.secapinventoryapi.domain.building.core.event.BuildingMeasuredEvent
import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingMeasurement
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingMeasurementRepository
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingRepository
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IReportRepository
import org.secapcompass.secapinventoryapi.domain.building.core.vo.MeasurementCalculation
import org.secapcompass.secapinventoryapi.domain.building.core.vo.ReportBatch
import org.secapcompass.secapinventoryapi.shared.domain.ICityRepository
import org.secapcompass.secapinventoryapi.shared.eventsourcing.EventMetadata
import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Component
import java.util.*
import kotlin.collections.ArrayList


// We will not listen building.measured
// We will listen building.measurement.calculated
// But for this bounded context, we can call it building measurements.


// TODO: Event icerisinde city Id de olsun, address pairdeki
@Component
class BuildingMeasuredProjection(
    private val buildingMeasurementRepository: IBuildingMeasurementRepository,
    private val buildingRepository: IBuildingRepository,
    private val cityRepository: ICityRepository,
    private val reportRepository: IReportRepository,
    private val gsonMapper: Gson,
    eventStoreDBPersistentSubscriptionsClient: EventStoreDBPersistentSubscriptionsClient,
    applicationConfiguration: ApplicationConfiguration,
    val buildingMeasurementList: ArrayList<BuildingMeasurement> = ArrayList<BuildingMeasurement>(),
    val transactionThreshold : Int = 10,
    val reportBatch: ReportBatch,
    val timeout:Long
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

            val building = buildingRepository.getBuildingById(UUID.fromString(event.event.streamId))
            if (building.isEmpty) {
                subscription.nack(NackAction.Park, "building not found")
                return
            }

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
            buildingMeasurementList.add(buildingMeasurement)

            val cityKey = cityRepository.getAllCities().filter { it.value.name==building.get().address.province }.map { it.key }.first()
            val city = cityRepository.getCityById(Integer.valueOf(cityKey))

            val districtKey = city.districts.filter { it.value.name == building.get().address.district }.map { it.key }.first
            val district = city.districts[districtKey]

            val cityId = String.format("%d_%d", cityKey, buildingMeasuredEvent.measurement.measurementDate.year)
            val districtId = String.format("%s_%s_%d", districtKey, district)

            try {
                getReportAndUpdate(cityId,buildingMeasuredEvent)
                //getReportAndUpdate(districtId,buildingMeasuredEvent)
                subscription.ack(event)
            }
            catch (e:RuntimeException){
                e.printStackTrace()
                when{
                    retryCount>3 -> subscription.nack(NackAction.Retry, "An error has occured during calculation, retrying")
                    else -> subscription.nack(NackAction.Park, "An error has occured during calculation, parking")
                }
            }
        }

        @Transactional
        @Lock(LockModeType.PESSIMISTIC_WRITE)
        private fun getReportAndUpdate(reportId:String,buildingMeasuredEvent: BuildingMeasuredEvent){
            if(reportBatch.isFirstCalculation){
                reportBatch.report = reportRepository.getById(reportId)
                reportBatch.isFirstCalculation = false
            }
            cancelReportBatchTimer()
            reportBatch.count += 1
            val reportData = reportBatch.report.data as MeasurementCalculation
            updateMeasurementData(reportData,buildingMeasuredEvent)
            startReportBatchTimer(timeout)
            if(reportBatch.count>=transactionThreshold){
                executeUpdateOnReport()
            }
        }

        private fun executeUpdateOnReport(){
            reportRepository.save(reportBatch.report)
            reportBatch.count = 0
            reportBatch.isFirstCalculation = true
        }

        private fun cancelReportBatchTimer(){
            reportBatch.timerTask.cancel()
            reportBatch.timer.cancel()
            reportBatch.timer.purge()
        }

        private fun startReportBatchTimer(timeout:Long){
            reportBatch.timer = Timer()
            reportBatch.timerTask = object : TimerTask() {
                override fun run() {
                    executeUpdateOnReport()
                }
            }
            reportBatch.timer.schedule(reportBatch.timerTask,0,timeout)
        }
        
        private fun updateMeasurementData(reportData:MeasurementCalculation, buildingMeasuredEvent: BuildingMeasuredEvent) {
            reportData.eF += buildingMeasuredEvent.measurement.measurementCalculation.eF
            reportData.cO2E += buildingMeasuredEvent.measurement.measurementCalculation.cO2E
            reportData.cO2 += buildingMeasuredEvent.measurement.measurementCalculation.cO2
            reportData.cH4 += buildingMeasuredEvent.measurement.measurementCalculation.cH4
            reportData.n2O += buildingMeasuredEvent.measurement.measurementCalculation.n2O
            reportData.bioFuelCO2 += buildingMeasuredEvent.measurement.measurementCalculation.bioFuelCO2
        }
    }
}

