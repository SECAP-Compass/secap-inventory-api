package org.secapcompass.secapinventoryapi.domain.building.core.repository

import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingMeasurement
import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementType
import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementTypeHeader
import org.secapcompass.secapinventoryapi.domain.building.core.model.Report
import org.secapcompass.secapinventoryapi.domain.building.core.vo.MeasurementDate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface IReportRepository {
    fun getById(id: String): Optional<Report>
    fun save(report: Report)
    fun getReportByFilter(
        cityId:String,
        startDate: MeasurementDate?,
        endDate: MeasurementDate?
    ): Report
}