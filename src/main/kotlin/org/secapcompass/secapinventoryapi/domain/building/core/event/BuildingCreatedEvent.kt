package org.secapcompass.secapinventoryapi.domain.building.core.event

import org.secapcompass.secapinventoryapi.domain.building.core.vo.Address

data class BuildingCreatedEvent(
    val address: Address,
)
