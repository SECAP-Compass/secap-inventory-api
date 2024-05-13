package org.secapcompass.secapinventoryapi.shared.eventsourcing

data class EventMetadata (
    val authority: String,
    val occurredBy: String
)