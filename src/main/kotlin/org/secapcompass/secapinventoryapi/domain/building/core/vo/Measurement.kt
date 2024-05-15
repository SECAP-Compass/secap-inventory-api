package org.secapcompass.secapinventoryapi.domain.building.core.vo

import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementType
import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementTypeHeader

data class Measurement(
    val value: Double,
    val unit: String,
    val measurementType: MeasurementType,
    val measurementTypeHeader: MeasurementTypeHeader
) {
    constructor(): this(0.0, "", MeasurementType.Electricity, MeasurementTypeHeader.Electricity)
}
