package org.secapcompass.secapinventoryapi.controller

import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingMeasurement
import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementType
import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementTypeHeader
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingMeasurementRepository
import org.secapcompass.secapinventoryapi.domain.building.infrastructure.dto.getMeasurement.GetMeasurementByFilterRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/measurements")
@CrossOrigin("*")
class MeasurementController(private val buildingMeasurementRepository: IBuildingMeasurementRepository) {

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
    @GetMapping("/filter/{buildingId}")
    fun getBuildingMeasurementsByFilter(
        @PathVariable buildingId:UUID,
        @RequestBody getMeasurementByFilterRequest: GetMeasurementByFilterRequest
    ): Page<BuildingMeasurement>{
        val pageable = PageRequest.of(getMeasurementByFilterRequest.page, getMeasurementByFilterRequest.size)
        return buildingMeasurementRepository.getBuildingMeasurementsByFilter(getMeasurementByFilterRequest.startDate,
            getMeasurementByFilterRequest.endDate,
            getMeasurementByFilterRequest.types,
            getMeasurementByFilterRequest.typeHeaders,
            getMeasurementByFilterRequest.gasTypes,
            pageable
        )
    }
    @GetMapping("/filter/{cityId}")
    fun getCityMeasurementByFilter(@PathVariable cityId:UUID) = null

    @GetMapping("/filter/{districtId}")
    fun getDistrictMeasurementByFilter(@PathVariable districtId:UUID) = null
}