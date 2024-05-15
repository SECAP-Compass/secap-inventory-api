package org.secapcompass.secapinventoryapi.domain.building.infrastructure

import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingMeasurement
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingMeasurementRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class BuildingMeasurementPsqlRepository(private val buildingMeasurementJpaRepository: BuildingMeasurementJpaRepository) : IBuildingMeasurementRepository {

    override fun saveBuildingMeasurement(buildingMeasurement: BuildingMeasurement): BuildingMeasurement {
        return buildingMeasurementJpaRepository.save(buildingMeasurement)
    }

    override fun getBuildingMeasurementById(id: UUID, pageable: Pageable): Page<BuildingMeasurement> {
        return buildingMeasurementJpaRepository.findAllByBuildingId(id, pageable)
    }
}

@Repository
interface BuildingMeasurementJpaRepository : JpaRepository<BuildingMeasurement, UUID> {

    fun findAllByBuildingId(buildingId: UUID, pageable: Pageable): Page<BuildingMeasurement>
}