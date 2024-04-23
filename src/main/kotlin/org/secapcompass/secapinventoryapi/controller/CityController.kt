package org.secapcompass.secapinventoryapi.controller

import org.secapcompass.secapinventoryapi.shared.domain.ICityRepository
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cities")
@CrossOrigin("*")
class CityController(private val cityRepository: ICityRepository) {

    @GetMapping
    fun getAllCities() = cityRepository.getAllCities()

    @GetMapping("/{id}")
    fun getCityById(@PathVariable id: Int) = cityRepository.getCityById(id)
}