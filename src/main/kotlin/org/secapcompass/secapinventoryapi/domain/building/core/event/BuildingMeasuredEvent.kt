package org.secapcompass.secapinventoryapi.domain.building.core.event

import kotlinx.serialization.Serializable
import org.secapcompass.secapinventoryapi.domain.building.core.vo.Measurement

@Serializable
data class BuildingMeasuredEvent(
    val measurement: Measurement
)
