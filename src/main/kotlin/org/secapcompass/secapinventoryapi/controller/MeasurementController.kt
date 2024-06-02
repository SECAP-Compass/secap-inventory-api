package org.secapcompass.secapinventoryapi.controller

import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingMeasurement
import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementType
import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementTypeHeader
import org.secapcompass.secapinventoryapi.domain.building.core.model.Report
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingMeasurementRepository
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IReportRepository
import org.secapcompass.secapinventoryapi.domain.building.core.vo.MeasurementDate
import org.secapcompass.secapinventoryapi.domain.building.infrastructure.ReportRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/measurements")
@CrossOrigin("*")
class MeasurementController(private val buildingMeasurementRepository: IBuildingMeasurementRepository,
    private val reportRepository: IReportRepository
) {

    @GetMapping("/types")
    fun getMeasurementTypes() = MeasurementType.values()

    @GetMapping("/typeHeaders")
    fun getMeasurementTypeHeader() = MeasurementTypeHeader.values()

    @GetMapping("/{id}")
    fun getMeasurementById(
        @PathVariable id:UUID,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ): Page<BuildingMeasurement>{
        val pageable = PageRequest.of(page, size)
        return buildingMeasurementRepository.getBuildingMeasurementById(id,pageable)
    }
    @GetMapping("/filter")
    fun getBuildingMeasurementsByFilter(
        @RequestParam buildingId:UUID,
        @RequestParam(required = false) startDateMonth:Short?,
        @RequestParam(required = false) startDateYear:Int?,
        @RequestParam(required = false) endDateMonth:Short?,
        @RequestParam(required = false) endDateYear:Int?,
        @RequestParam(required = false) types: List<MeasurementType>?,
        @RequestParam(required = false) typeHeaders: List<MeasurementTypeHeader>?,
        @RequestParam(required = false) gasTypes: List<String>?,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ): Page<BuildingMeasurement>{
        val pageable = PageRequest.of(page, size)

        val startDate = MeasurementDate(startDateMonth,startDateYear);
        val endDate = MeasurementDate(endDateMonth,endDateYear);

        return buildingMeasurementRepository.getBuildingMeasurementsByFilter(buildingId,
            startDate,
            endDate,
            types,
            typeHeaders,
            gasTypes,
            pageable
        )
    }
    @GetMapping("/filter/city}")
    fun getCityMeasurementByFilter(
        @RequestParam(required = false) cityId:String,
        @RequestParam(required = false) startDateMonth: Short?,
        @RequestParam(required = false) startDateYear: Int?,
        @RequestParam(required = false) endDateYear: Int?,
        @RequestParam(required = false) endDateMonth:Short?,
    ) : Report {
        val startDate = MeasurementDate(startDateMonth,startDateYear);
        val endDate = MeasurementDate(endDateMonth,endDateYear);

        return reportRepository.getById(cityId).orElseThrow();
    }

    @GetMapping("/filter/{districtId}")
    fun getDistrictMeasurementByFilter(@PathVariable districtId:UUID) = null
}