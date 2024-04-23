package org.secapcompass.secapinventoryapi.controller

import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/measurements")
@CrossOrigin("*")
class MeasurementController {

    @GetMapping("/types")
    fun getMeasurementTypes() = MeasurementType.values()
}