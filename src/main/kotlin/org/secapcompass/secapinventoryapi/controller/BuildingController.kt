package org.secapcompass.secapinventoryapi.controller

import org.secapcompass.secapinventoryapi.domain.building.core.model.Building
import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingMeasurement
import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingType
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingMeasurementRepository
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingRepository
import org.secapcompass.secapinventoryapi.domain.building.core.vo.Address
import org.secapcompass.secapinventoryapi.domain.building.exception.BuildingNotFoundException
import org.secapcompass.secapinventoryapi.shared.domain.ICityRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RequestMapping("/buildings")
@RestController
class BuildingController(
    private val buildingRepository: IBuildingRepository,
    private val measurementsRepository: IBuildingMeasurementRepository,
    private val cityRepository: ICityRepository
) {

    @GetMapping
    fun getBuildings(
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ): Page<Building> {
        val pageable = PageRequest.of(page, size)

        return buildingRepository.getBuildings(pageable)
    }

    @GetMapping("/{id}")
    fun getBuildingById(@PathVariable id: UUID): Building {
        return buildingRepository.getBuildingById(id)
            .orElseThrow { BuildingNotFoundException("building.not.found") }
    }

    @GetMapping("/{id}/measurements")
    fun getBuildingMeasurements(
        @PathVariable id: UUID,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ): Page<BuildingMeasurement> {
         val pageable = PageRequest.of(page, size)

        return measurementsRepository.getBuildingMeasurementById(id, pageable)
    }

    @GetMapping("/filter")
    fun getBuildingsByFilter(
        @RequestParam(required = false, defaultValue = "Turkey") country: String,
        @RequestParam(required = false) region: String?,
        @RequestParam(required = false) cityId: Int?,
        @RequestParam(required = false) districtId: String?,
        @RequestParam(required = false) type: BuildingType?,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ): Page<Building> {
        val pageable = PageRequest.of(page, size)


        val city = cityId?.let { cityRepository.getCityById(it) }


        val address = Address(
            country = country,
            region = "Marmara",
            province =  city?.name,
            district = districtId?.let { city?.districts?.get(it)?.name }
        )
        return buildingRepository.getBuildingsByFilter(
            address,
            type,
            pageable
        )
    }
}