package org.secapcompass.secapinventoryapi.domain.building.infrastructure

import org.secapcompass.secapinventoryapi.domain.building.core.model.Building
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingPsqlRepo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class BuildingPsqlRepository(private val buildingJpaRepository: BuildingJpaRepository) : IBuildingPsqlRepo{

    override fun saveBuilding(building: Building): Building {
        return buildingJpaRepository.save(building)
    }

    override fun getBuildingById(id: UUID): Building {
        return buildingJpaRepository.findById(id).orElseThrow { throw RuntimeException("building not found") }
    }
}

@Repository
interface BuildingJpaRepository : JpaRepository<Building, UUID> {
}