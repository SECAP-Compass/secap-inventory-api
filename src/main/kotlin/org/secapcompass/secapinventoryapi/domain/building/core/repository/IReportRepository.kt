package org.secapcompass.secapinventoryapi.domain.building.core.repository

import org.secapcompass.secapinventoryapi.domain.building.core.model.Report

interface IReportRepository {
    fun getById(id: String): Report
    fun save(report: Report)
}