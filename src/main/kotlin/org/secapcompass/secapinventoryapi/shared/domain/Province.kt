package org.secapcompass.secapinventoryapi.shared.domain

import jakarta.persistence.Embeddable
import kotlinx.serialization.Serializable

@Serializable
data class Province(
    val id: Long,
    val name: String,
    val population: Int,
    val coordinates: Map<String, Double>,
    val maps: Map<String, String>,
    val region: Map<String, String>,
    val districts: Map<String, District>,
)

@Serializable
data class District(
    val id: Long,
    val name: String,
    val area: Long,
    val population: Long,
    val provinceId: Long,
)

typealias Provinces = Map<String, Province>