package org.secapcompass.secapinventoryapi.domain.building.core.repository

import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingMeasurementCalculation
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface IBuildingMeasurementCalculationRepository {
    fun saveBuildingMeasurementCalculation(buildingMeasurementCalculation:BuildingMeasurementCalculation): BuildingMeasurementCalculation
    fun getBuildingMeasurementCalculationById(id: UUID, pageable: Pageable): Page<BuildingMeasurementCalculation>
}