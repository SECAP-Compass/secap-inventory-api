package org.secapcompass.secapinventoryapi.domain.building.core.repository

import org.secapcompass.secapinventoryapi.domain.building.core.model.Building
import java.util.UUID

interface IBuildingPsqlRepo {

    fun saveBuilding(building: Building): Building
    fun getBuildingById(id: UUID): Building
}