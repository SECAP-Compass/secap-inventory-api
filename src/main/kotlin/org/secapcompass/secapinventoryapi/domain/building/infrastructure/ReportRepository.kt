package org.secapcompass.secapinventoryapi.domain.building.infrastructure

import org.secapcompass.secapinventoryapi.domain.building.core.model.Report
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IReportRepository
import org.secapcompass.secapinventoryapi.domain.building.exception.ReportNotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class ReportRepository(private val reportJpaRepository: ReportJpaRepository): IReportRepository {

    override fun getById(id: String): Optional<Report> {
        return reportJpaRepository.findById(id)
    }

    override fun save(report: Report) {
        reportJpaRepository.save(report)
    }
}

interface ReportJpaRepository : JpaRepository<Report, String>