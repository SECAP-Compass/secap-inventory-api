package org.secapcompass.secapinventoryapi.domain.building.core.vo

import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementType

data class Measurement(
    val value: Double,
    val unit: String,
    val measurementType: MeasurementType
) {
    constructor(): this(0.0, "", MeasurementType.Electricity)
}
