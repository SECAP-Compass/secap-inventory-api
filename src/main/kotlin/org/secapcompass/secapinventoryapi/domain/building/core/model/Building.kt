package org.secapcompass.secapinventoryapi.domain.building.core.model

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.secapcompass.secapinventoryapi.domain.building.core.vo.Address
import java.util.UUID

@Entity
data class Building(
    @Id var id: UUID? = null,
    @Embedded val address: Address,
    val area: Double
)
