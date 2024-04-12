package org.secapcompass.secapinventoryapi.domain.building.core.vo

import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val country: String,
    val region: String,
    val province: String,
    val district: String,
    val postalCode: String,
) {
    // No-argument constructor for JPA/Hibernate
    constructor() : this("", "", "", "", "")
}