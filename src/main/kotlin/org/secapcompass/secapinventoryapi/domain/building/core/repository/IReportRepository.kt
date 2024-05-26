package org.secapcompass.secapinventoryapi.domain.building.core.repository

import org.secapcompass.secapinventoryapi.domain.building.core.model.Report
import java.util.*

interface IReportRepository {
    fun getById(id: String): Optional<Report>
    fun save(report: Report)
}