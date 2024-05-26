package org.secapcompass.secapinventoryapi.domain.building.core.model

import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id


import org.secapcompass.secapinventoryapi.configuration.CalculationConverter
import org.secapcompass.secapinventoryapi.domain.building.core.vo.MeasurementCalculation

import java.time.Instant


@Entity
data class Report(
    @Id val id: String,
    @Convert(converter = CalculationConverter::class)
    var data: MeasurementCalculation?,
    val lastModifiedDate: Instant = Instant.now(),
)