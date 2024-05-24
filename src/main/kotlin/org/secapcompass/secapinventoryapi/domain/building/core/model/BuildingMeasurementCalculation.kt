package org.secapcompass.secapinventoryapi.domain.building.core.model

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.secapcompass.secapinventoryapi.domain.building.core.vo.MeasurementCalculation
import java.util.*


data class BuildingMeasurementCalculation(
    @Id val id: UUID,
    val buildingId: UUID,
    @Embedded val measurementCalculation: MeasurementCalculation,
    val createdBy: String,
)