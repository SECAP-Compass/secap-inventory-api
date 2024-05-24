package org.secapcompass.secapinventoryapi.domain.building.core.event

import kotlinx.serialization.Serializable
import org.secapcompass.secapinventoryapi.domain.building.core.vo.Address
import java.time.Month
import java.time.Year

data class BuildingCreatedEvent(
    val address: AddressDTO,
    val area: Area,
    val type: String,
    val buildingCreateDate: BuildingCreateDate
)

data class Area(
    val value: Double,
    val unit: String
) {
    constructor(): this(0.0, "")
}

data class AddressDTO(
    val country: String,
    val region: String,
    val province: AddressPair,
    val district: AddressPair,
)

data class AddressPair(
    val id: Int,
    val value: String
)

data class BuildingCreateDate(
    val createYear: Year,
    val createMonth: Month
)

fun AddressDTO.toAddress(): Address {
    return Address(
        country = this.country,
        region = this.region,
        province = this.province.value,
        district = this.district.value
    )
}