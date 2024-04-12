package org.secapcompass.secapinventoryapi.domain.building.core.event

import kotlinx.serialization.Serializable
import org.secapcompass.secapinventoryapi.domain.building.core.vo.Address

@Serializable
data class BuildingCreatedEvent(
    val address: Address
)