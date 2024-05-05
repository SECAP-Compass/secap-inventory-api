package org.secapcompass.secapinventoryapi.domain.building.core.event

import org.secapcompass.secapinventoryapi.domain.building.core.vo.Address

data class BuildingCreatedEvent(
    val address: AddressDTO,
)

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

fun AddressDTO.toAddress(): Address {
    return Address(
        country = this.country,
        region = this.region,
        province = this.province.value,
        district = this.district.value
    )
}