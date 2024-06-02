package org.secapcompass.secapinventoryapi.domain.building.infrastructure

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.secapcompass.secapinventoryapi.domain.building.core.model.Report
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IReportRepository
import org.secapcompass.secapinventoryapi.domain.building.core.vo.MeasurementDate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
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

    override fun getReportByFilter(
        cityId: String,
        startDate: MeasurementDate?,
        endDate: MeasurementDate?): Report {
        return reportJpaRepository
            .findById(cityId).orElseThrow()
    }
}

@Repository
interface ReportJpaRepository : JpaRepository<Report, String>, JpaSpecificationExecutor<Report>{

}
