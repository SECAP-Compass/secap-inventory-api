package org.secapcompass.secapinventoryapi.domain.building.core.repository

import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingMeasurement
import java.util.UUID

interface IBuildingMeasurementRepository {

    fun saveBuildingMeasurement(buildingMeasurement: BuildingMeasurement): BuildingMeasurement
    fun getBuildingMeasurementById(id: UUID): BuildingMeasurement
}
