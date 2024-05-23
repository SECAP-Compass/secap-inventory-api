package org.secapcompass.secapinventoryapi.domain.building.core.vo

import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementType
import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementTypeHeader
import java.time.Month
import java.time.Year
import java.util.*

data class Measurement(
    val value: Double,
    val unit: String,
    val measurementType: MeasurementType,
    val measurementTypeHeader: MeasurementTypeHeader,
    val measurementDate: MeasurementDate,
    val measurementCalculation: MeasurementCalculation
) {
    constructor(): this(0.0, "", MeasurementType.Electricity, MeasurementTypeHeader.Electricity,
        MeasurementDate(Month.MAY,Year.now()),
        MeasurementCalculation(10.0,10.0,10.0,10.0,10.0,10.0))
}

data class MeasurementDate(
    val month: Month,
    val year: Year
)
