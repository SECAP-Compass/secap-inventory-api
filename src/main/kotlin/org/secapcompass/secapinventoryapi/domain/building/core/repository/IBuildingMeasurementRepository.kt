package org.secapcompass.secapinventoryapi.domain.building.core.repository

import org.secapcompass.secapinventoryapi.domain.building.core.event.BuildingMeasurementDate
import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingMeasurement
import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementType
import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementTypeHeader
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface IBuildingMeasurementRepository {

    fun saveBuildingMeasurement(buildingMeasurement: BuildingMeasurement): BuildingMeasurement
    fun getBuildingMeasurementById(id: UUID, pageable: Pageable): Page<BuildingMeasurement>
    fun getBuildingMeasurementsBetweenDates(startDate: BuildingMeasurementDate,
                                            endDate: BuildingMeasurementDate,
                                            pageable: Pageable): Page<BuildingMeasurement>
    fun getBuildingMeasurementsByFilter(types:List<MeasurementType>?,
                                        typeHeaders:List<MeasurementTypeHeader>,
                                        pageable: Pageable): Page<BuildingMeasurement>

    fun getBuildingMeasurementBetweenDatesByFilter(startDate: BuildingMeasurementDate,
                                                   endDate: BuildingMeasurementDate,
                                                   types:List<MeasurementType>?,
                                                   typeHeaders:List<MeasurementTypeHeader>,
                                                   pageable: Pageable): Page<BuildingMeasurement>
}
