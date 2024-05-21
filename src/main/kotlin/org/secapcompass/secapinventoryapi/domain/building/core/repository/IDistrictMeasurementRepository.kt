package org.secapcompass.secapinventoryapi.domain.building.core.repository

import org.secapcompass.secapinventoryapi.domain.building.core.event.AddressPair
import org.secapcompass.secapinventoryapi.domain.building.core.event.BuildingMeasurementDate
import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingMeasurement
import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementType
import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementTypeHeader
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface IDistrictMeasurementRepository {

    fun getDistrictMeasurements(district: AddressPair, pageable: Pageable): Page<BuildingMeasurement>

    fun getDistrictMeasurementsBetweenDates(district:AddressPair, startDate:BuildingMeasurementDate,
                                            endDate: BuildingMeasurementDate,
                                            pageable: Pageable): Page<BuildingMeasurement>

    fun getDistrictMeasurementsByFilter(district:AddressPair, types:List<MeasurementType>?,
                                        typeHeaders:List<MeasurementTypeHeader>,
                                        pageable: Pageable): Page<BuildingMeasurement>

    fun getDistrictMeasurementsBetweenDatesByFilter(district:AddressPair, startDate:BuildingMeasurementDate,
                                                    endDate: BuildingMeasurementDate,
                                                    types:List<MeasurementType>?,
                                                    typeHeaders:List<MeasurementTypeHeader>,
                                                    pageable: Pageable): Page<BuildingMeasurement>
}