package org.secapcompass.secapinventoryapi.shared.eventsourcing

import kotlinx.serialization.Serializable

@Serializable
data class EventMetadata (
    val authority: String,
    val occurredBy: String
)