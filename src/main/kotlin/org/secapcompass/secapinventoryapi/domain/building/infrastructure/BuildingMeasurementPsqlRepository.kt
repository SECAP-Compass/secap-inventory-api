package org.secapcompass.secapinventoryapi.domain.building.infrastructure

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingMeasurement
import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingType
import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementType
import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementTypeHeader
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingMeasurementRepository
import org.secapcompass.secapinventoryapi.domain.building.core.vo.Measurement
import org.secapcompass.secapinventoryapi.domain.building.core.vo.MeasurementCalculation
import org.secapcompass.secapinventoryapi.domain.building.core.vo.MeasurementDate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class BuildingMeasurementPsqlRepository(private val buildingMeasurementJpaRepository: BuildingMeasurementJpaRepository) :
    IBuildingMeasurementRepository {

    override fun saveBuildingMeasurement(buildingMeasurement: BuildingMeasurement): BuildingMeasurement {
        return buildingMeasurementJpaRepository.save(buildingMeasurement)
    }

    override fun getBuildingMeasurementById(id: UUID, pageable: Pageable): Page<BuildingMeasurement> {
        return buildingMeasurementJpaRepository.findAllByBuildingId(id, pageable)
    }

    override fun getBuildingMeasurementsByFilter(
        buildingId: UUID,
        startDate: MeasurementDate?,
        endDate: MeasurementDate?,
        types: List<MeasurementType>?,
        typeHeaders: List<MeasurementTypeHeader>?,
        gasTypes: List<String>?,
        pageable: Pageable
    ): Page<BuildingMeasurement> {
        return buildingMeasurementJpaRepository
            .findAll(
                BuildingMeasurementSpecification
                    .filterBuildingMeasurement(buildingId,startDate, endDate, types, typeHeaders, gasTypes), pageable
            )
    }


}

@Repository
interface BuildingMeasurementJpaRepository : JpaRepository<BuildingMeasurement, UUID>,
    JpaSpecificationExecutor<BuildingMeasurement> {

    fun findAllByBuildingId(buildingId: UUID, pageable: Pageable): Page<BuildingMeasurement>
    fun findByMeasurementMeasurementType(
        measurementType: MeasurementType,
        pageable: Pageable
    ): Page<BuildingMeasurement>

    fun findByMeasurementMeasurementTypeHeaderIn(
        measurementTypeHeader: List<MeasurementTypeHeader>,
        pageable: Pageable
    ): Page<BuildingMeasurement>

}

class BuildingMeasurementSpecification {
    companion object {

        fun filterBuildingMeasurement(
            buildingId: UUID,
            startDate: MeasurementDate?,
            endDate: MeasurementDate?,
            types: List<MeasurementType>?,
            typeHeaders: List<MeasurementTypeHeader>?,
            gasTypes: List<String>?
        ): Specification<BuildingMeasurement> {
            return Specification { root: Root<BuildingMeasurement>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                val predicates = mutableListOf<Predicate>()

                predicates.add(criteriaBuilder.equal(root.get<BuildingMeasurement>("buildingId"), buildingId))

                if (types != null) {
                    if (types.isNotEmpty()) {
                        types.forEach {
                            predicates
                                .add(
                                    criteriaBuilder.equal(
                                        root
                                            .get<Measurement>("measurement")
                                            .get<MeasurementType>("measurementType"), it
                                    )
                                )
                        }
                    }
                }


                if (typeHeaders != null) {
                    if (typeHeaders.isNotEmpty()) {
                        typeHeaders.forEach {
                            predicates
                                .add(
                                    criteriaBuilder.equal(
                                        root
                                            .get<Measurement>("measurement")
                                            .get<MeasurementTypeHeader>("measurementTypeHeader"), it
                                    )
                                )
                        }
                    }
                }

                if (startDate != null && endDate != null){
                    if (startDate.year != null &&
                        startDate.month != null &&
                        endDate.year != null &&
                        endDate.month != null){
                        predicates.add(
                            criteriaBuilder.between(root
                                .get<Measurement>("measurement")
                                .get<MeasurementDate>("measurementDate")
                                .get("year"),startDate.year,endDate.year)
                        )
                        predicates.add(
                            criteriaBuilder.between(root
                                .get<Measurement>("measurement")
                                .get<MeasurementDate>("measurementDate")
                                .get("month"),startDate.month,endDate.month)
                        )
                    }
                }
                if (gasTypes != null) {
                    if (gasTypes.isNotEmpty()) {
                        gasTypes.forEach { gasType ->
                            predicates.add(
                                criteriaBuilder.equal(
                                    root
                                        .get<Measurement>("measurement")
                                        .get<MeasurementCalculation>("measurementCalculation"), gasType
                                )
                            )
                        }
                    }
                }
                criteriaBuilder.and(*predicates.toTypedArray())
            }
        }
    }
}