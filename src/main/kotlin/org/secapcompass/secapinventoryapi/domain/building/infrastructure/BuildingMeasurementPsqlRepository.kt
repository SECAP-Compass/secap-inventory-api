package org.secapcompass.secapinventoryapi.domain.building.infrastructure

import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingMeasurement
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingMeasurementRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class BuildingMeasurementPsqlRepository(private val buildingMeasurementJpaRepository: BuildingMeasurementJpaRepository) : IBuildingMeasurementRepository {

    override fun saveBuildingMeasurement(buildingMeasurement: BuildingMeasurement): BuildingMeasurement {
        return buildingMeasurementJpaRepository.save(buildingMeasurement)
    }

    override fun getBuildingMeasurementById(id: UUID): BuildingMeasurement {
        return buildingMeasurementJpaRepository.findById(id).orElseThrow { RuntimeException("Building Measurement not found") }
    }
}

@Repository
interface BuildingMeasurementJpaRepository : JpaRepository<BuildingMeasurement, UUID> {
}