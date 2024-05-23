package org.secapcompass.secapinventoryapi.domain.building.core.repository

import org.secapcompass.secapinventoryapi.domain.building.core.model.Building
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface IDistrictRepository {
    fun getDistrictBuildingsByFilter(districtId:Int? ,pageable: Pageable): Page<Building>
}