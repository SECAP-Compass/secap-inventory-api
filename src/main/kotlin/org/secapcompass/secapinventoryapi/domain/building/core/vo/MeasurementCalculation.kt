package org.secapcompass.secapinventoryapi.domain.building.core.vo


data class MeasurementCalculation(
    var CO2: Double,
    var CH4: Double,
    var N2O: Double,
    var CO2e: Double,
    var BiofuelCO2: Double,
    var EF: Double
){
    constructor(): this(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
}