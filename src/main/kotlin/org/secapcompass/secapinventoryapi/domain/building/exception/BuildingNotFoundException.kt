package org.secapcompass.secapinventoryapi.domain.building.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class BuildingNotFoundException(
    override val message: String
): ResponseStatusException(HttpStatus.NOT_FOUND, message)

class ReportNotFoundException(
    override val message: String
): ResponseStatusException(HttpStatus.NOT_FOUND, message)
