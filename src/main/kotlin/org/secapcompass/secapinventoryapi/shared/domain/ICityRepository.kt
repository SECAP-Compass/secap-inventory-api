package org.secapcompass.secapinventoryapi.shared.domain

interface ICityRepository {
    fun getCityById(id: Int): Province
    fun getAllCities(): Map<String, Province>
}