package org.secapcompass.secapinventoryapi.domain.building.infrastructure

import org.secapcompass.secapinventoryapi.domain.building.core.model.Building
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
class BuildingPsqlRepository(private val buildingJpaRepository: BuildingJpaRepository) : IBuildingRepository {

    override fun saveBuilding(building: Building): Building {
        return buildingJpaRepository.save(building)
    }

    override fun getBuildingById(id: UUID): Optional<Building> {
        return buildingJpaRepository.findById(id)
    }

    override fun getBuildings(pageable: Pageable): Page<Building> {
        return buildingJpaRepository.findAll(pageable)
    }
}

@Repository
interface BuildingJpaRepository : JpaRepository<Building, UUID>
