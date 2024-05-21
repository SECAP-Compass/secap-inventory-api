package org.secapcompass.secapinventoryapi.domain.building.core.event

import org.secapcompass.secapinventoryapi.domain.building.core.vo.Measurement
import java.time.Month
import java.time.Year

data class BuildingMeasuredEvent(
    val measurement: Measurement,
    val buildingMeasurementDate: BuildingMeasurementDate
)

data class BuildingMeasurementDate(
    val measurementMonth: Month,
    val measurementYear: Year
)





