package org.secapcompass.secapinventoryapi.domain.building.infrastructure.dto.getMeasurement

import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementType
import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementTypeHeader
import org.secapcompass.secapinventoryapi.domain.building.core.vo.MeasurementDate

data class GetMeasurementByFilterRequest(
    val startDate: MeasurementDate,
    val endDate: MeasurementDate,
    val types: List<MeasurementType>,
    val typeHeaders: List<MeasurementTypeHeader>,
    val page: Int = 0,
    val size: Int = 10
)