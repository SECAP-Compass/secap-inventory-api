package org.secapcompass.secapinventoryapi.domain.building.core.event

import org.secapcompass.secapinventoryapi.domain.building.core.vo.Measurement

data class BuildingMeasuredEvent(val measurement: Measurement)
