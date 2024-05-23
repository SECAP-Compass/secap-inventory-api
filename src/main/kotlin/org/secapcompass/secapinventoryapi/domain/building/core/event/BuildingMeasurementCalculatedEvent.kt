package org.secapcompass.secapinventoryapi.domain.building.core.event

import org.secapcompass.secapinventoryapi.domain.building.core.vo.MeasurementCalculation

class BuildingMeasurementCalculatedEvent(
    val calculatedMeasurement: MeasurementCalculation
) {
}