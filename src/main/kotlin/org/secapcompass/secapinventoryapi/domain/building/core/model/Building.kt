package org.secapcompass.secapinventoryapi.domain.building.core.model

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.secapcompass.secapinventoryapi.domain.building.core.event.Area
import org.secapcompass.secapinventoryapi.domain.building.core.vo.Address
import java.util.UUID

@Entity
data class Building(
    @Id var id: UUID? = null,
    @Embedded val address: Address,
    @Embedded val area: Area,
    val type: BuildingType,
    val createdBy: String,
)
