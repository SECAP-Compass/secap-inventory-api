package org.secapcompass.secapinventoryapi.domain.building.core.vo

import kotlinx.serialization.Serializable

@Serializable
data class MeasurementCalculation(
    var cO2: Double,
    var cH4: Double,
    var n2O: Double,
    var cO2E: Double,
    var bioFuelCO2: Double,
    var eF: Double
)