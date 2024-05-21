package org.secapcompass.secapinventoryapi.domain.building.core.repository

import org.secapcompass.secapinventoryapi.domain.building.core.model.Building
import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingType
import org.secapcompass.secapinventoryapi.domain.building.core.vo.Address
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional
import java.util.UUID

interface IBuildingRepository {
    fun saveBuilding(building: Building): Building

    fun getBuildings(pageable: Pageable): Page<Building>
    fun getBuildingById(id: UUID): Optional<Building>
    fun getBuildingsByFilter(address: Address, buildingType: BuildingType?, pageable: Pageable): Page<Building>

}
