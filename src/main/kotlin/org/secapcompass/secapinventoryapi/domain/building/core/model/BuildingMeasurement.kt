package org.secapcompass.secapinventoryapi.domain.building.core.model

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.secapcompass.secapinventoryapi.domain.building.core.vo.Measurement
import java.util.UUID

@Entity
data class BuildingMeasurement(
    @Id val id: UUID,
    val buildingId: UUID,
    @Embedded val measurement: Measurement,
    val createdBy: String,
)
