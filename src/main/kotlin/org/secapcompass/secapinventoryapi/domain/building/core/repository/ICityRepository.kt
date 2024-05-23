package org.secapcompass.secapinventoryapi.domain.building.core.repository

import org.secapcompass.secapinventoryapi.domain.building.core.event.AddressPair
import org.secapcompass.secapinventoryapi.domain.building.core.model.Building
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface ICityRepository {
    fun getCityBuildingsByFilter(cityId:Int? ,pageable: Pageable): Page<Building>
    fun getCityDistrictsByFilter(cityId: Int?, pageable: Pageable): Page<AddressPair>
}