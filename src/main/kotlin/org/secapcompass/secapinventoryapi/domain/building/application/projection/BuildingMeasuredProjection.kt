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
import org.secapcompass.secapinventoryapi.domain.building.core.model.Report
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
) {
    private val transactionThreshold : Int = 10
    private var reportBatchCity: ReportBatch = ReportBatch()
    private var reportBatchDistrict: ReportBatch = ReportBatch()
    private var timeout: Long = 100



    private val logger = LoggerFactory.getLogger(BuildingMeasuredProjection::class.java)
    init {
        val EVENT_TYPE_PREFIX = "building.measurement.calculated"
        val filter = SubscriptionFilter.newBuilder().addEventTypePrefix(EVENT_TYPE_PREFIX).build()

        reportBatchCity.setTimerTask(::executeUpdateOnReport)
        reportBatchCity.setTimerTask(::executeUpdateOnReport)

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

            val cityKey = cityRepository.getAllCities().filter { it.value.name==building.get().address.province }.map { it.key }.first()
            val city = cityRepository.getCityById(Integer.valueOf(cityKey))

            val districtKey = city.districts.filter { it.value.name == building.get().address.district }.map { it.key }.first()
            val district = city.districts[districtKey]

            val cityId = String.format("%s_%d", cityKey, buildingMeasuredEvent.measurement.measurementDate.year)
            val districtId = String.format("%s_%s_%d", districtKey, district)


            try {
                /*
                * these two could be handled outside of same try block
                * */
                getReportAndUpdate(cityId, buildingMeasuredEvent,reportBatchCity)
                getReportAndUpdate(districtId, buildingMeasuredEvent,reportBatchDistrict)
                subscription.ack(event)
            }
            catch (e:RuntimeException){
                e.printStackTrace()
                if (retryCount > 2) {
                    subscription.nack(NackAction.Park, "failed to deserialize event data")
                } else {
                    subscription.nack(NackAction.Retry, "failed to deserialize event data")
                }
            }
        }
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun getReportAndUpdate(reportId: String, buildingMeasuredEvent: BuildingMeasuredEvent, reportBatch: ReportBatch){

        if (reportBatch.count < 1) {
            reportBatch.report = reportRepository.getById(reportId).orElseGet { Report(reportId,null) }
        }

        reportBatch.count += 1

        val calculation: MeasurementCalculation = if (reportBatch.report?.data == null) {
            MeasurementCalculation()
        } else {
            reportBatch.report!!.data!! as MeasurementCalculation
        }

        updateMeasurementData(calculation, buildingMeasuredEvent)
        reportBatch.cancelReportBatchTimer()
        reportBatch.startReportBatchTimer(timeout)

        reportBatch.report?.data = calculation

        if(reportBatch.count >= transactionThreshold){
            executeUpdateOnReport(reportBatch)
        }
    }

    private fun executeUpdateOnReport(reportBatch: ReportBatch){
        if (reportBatch.count > 0) {
            reportRepository.save(reportBatch.report!!)
            reportBatch.count = 0
        }
    }

    private fun updateMeasurementData(reportData:MeasurementCalculation, buildingMeasuredEvent: BuildingMeasuredEvent) {
        reportData.EF += buildingMeasuredEvent.measurement.measurementCalculation.EF
        reportData.CO2e += buildingMeasuredEvent.measurement.measurementCalculation.CO2e
        reportData.CO2 += buildingMeasuredEvent.measurement.measurementCalculation.CO2
        reportData.CH4 += buildingMeasuredEvent.measurement.measurementCalculation.CH4
        reportData.N2O += buildingMeasuredEvent.measurement.measurementCalculation.N2O
        reportData.BiofuelCO2 += buildingMeasuredEvent.measurement.measurementCalculation.BiofuelCO2
    }
}

