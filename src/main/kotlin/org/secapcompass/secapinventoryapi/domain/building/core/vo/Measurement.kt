package org.secapcompass.secapinventoryapi.domain.building.core.vo

import jakarta.persistence.Embedded
import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementType
import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementTypeHeader


data class Measurement(
    val value: Double,
    val unit: String,
    val measurementType: MeasurementType,
    val measurementTypeHeader: MeasurementTypeHeader,
    @Embedded val measurementDate: MeasurementDate,
    @Embedded val measurementCalculation: MeasurementCalculation
) {
    constructor(): this(0.0, "", MeasurementType.Electricity, MeasurementTypeHeader.Electricity,
        MeasurementDate(1, 1),
        MeasurementCalculation(10.0,10.0,10.0,10.0,10.0,10.0))
}

data class MeasurementDate(
    val month: Short,
    val year: Int,
) {
    constructor(): this(0, 0)
}
