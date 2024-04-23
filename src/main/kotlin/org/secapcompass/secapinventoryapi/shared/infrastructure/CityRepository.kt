package org.secapcompass.secapinventoryapi.shared.infrastructure

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.secapcompass.secapinventoryapi.shared.domain.Province
import org.secapcompass.secapinventoryapi.shared.domain.ICityRepository
import org.secapcompass.secapinventoryapi.shared.domain.Provinces
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

@Component
class CityRepository : ICityRepository {

    lateinit var provinces: Provinces

    init {
        ClassPathResource("config/cities.json").inputStream.also {
            provinces = Json{ignoreUnknownKeys = true}
                .decodeFromStream(it)
        }

        LoggerFactory.getLogger(CityRepository::class.java).info("Cities loaded")
    }

    override fun getCityById(id: Int): Province {
        return provinces[id.toString()] ?: throw IllegalArgumentException("City not found")
    }

    override fun getAllCities(): Map<String, Province> {
        return provinces
    }
}