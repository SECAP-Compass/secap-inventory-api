package org.secapcompass.secapinventoryapi.domain.building.core.model

import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.secapcompass.secapinventoryapi.configuration.JsonConverter

import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant


@Entity
data class Report(
    @Id val id: String,
    @Convert(converter = JsonConverter::class) val data: Any,
    @LastModifiedDate val lastModifiedDate: Instant = Instant.now(),
)