package org.secapcompass.secapinventoryapi.domain.building.core.vo

import kotlinx.serialization.Serializable

@Serializable
data class MeasurementCalculation(
    val cO2: Double,
    val cH4: Double,
    val n2O: Double,
    val cO2E: Double,
    val bioFuelCO2: Double,
    val eF: Double
)