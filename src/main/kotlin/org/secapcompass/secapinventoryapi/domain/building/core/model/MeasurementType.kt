package org.secapcompass.secapinventoryapi.domain.building.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class MeasurementType(s: String) {
    Electricity("Electricity"),

    DistrictHeating("District Heating"),
    DistrictCooling("District Cooling"),

    NaturalGas("Natural Gas"),
    LiquidGas("Liquid Gas"),
    HeatingOil("Heating Oil"),
    Diesel("Diesel"),
    Gasoline("Gasoline"),
    Lignite("Lignite"),
    Coal("Coal"),
    OtherFossilFuels("Other Fossil Fuels"),

    Biogas("Biogas"),
    PlantOil("Plant Oil"),
    Biofuel("Biofuel"),
    OtherBiomass("Other Biomass"),

    SolarThermal("Solar Thermal"),
    Geothermal("Geothermal"),
}

@Serializable
enum class MeasurementTypeHeader(s: String) {
    Electricity("Electricity"),
    DistrictHeatingAndCooling("District heating and cooling"),
    FossilFuels("Fossil fuels"),
    RenewableEnergies("Renewable energies")
}