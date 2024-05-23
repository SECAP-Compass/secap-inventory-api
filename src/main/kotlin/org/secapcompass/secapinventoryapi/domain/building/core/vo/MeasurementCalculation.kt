package org.secapcompass.secapinventoryapi.domain.building.core.vo

data class MeasurementCalculation(
    val cO2: Double,
    val cH4: Double,
    val n2O: Double,
    val cO2E: Double,
    val bioFuelCO2: Double,
    val eF: Double
){

}

/*
*
*  CO2        float64 json:"CO2"
    CH4        float64 json:"CH4"
    N2O        float64 json:"N2O"
    CO2e       float64 json:"CO2e"
    BiofuelCO2 float64 json:"BiofuelCO2"
    EF         float64 json:"EF"
* */