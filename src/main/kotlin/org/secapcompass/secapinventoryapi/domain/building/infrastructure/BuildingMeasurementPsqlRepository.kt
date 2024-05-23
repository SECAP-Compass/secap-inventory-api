package org.secapcompass.secapinventoryapi.domain.building.infrastructure

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.secapcompass.secapinventoryapi.domain.building.core.model.*
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingMeasurementRepository
import org.secapcompass.secapinventoryapi.domain.building.core.vo.Measurement
import org.secapcompass.secapinventoryapi.domain.building.core.vo.MeasurementDate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class BuildingMeasurementPsqlRepository(private val buildingMeasurementJpaRepository: BuildingMeasurementJpaRepository) : IBuildingMeasurementRepository {

    override fun saveBuildingMeasurement(buildingMeasurement: BuildingMeasurement): BuildingMeasurement {
        return buildingMeasurementJpaRepository.save(buildingMeasurement)
    }

    override fun getBuildingMeasurementById(id: UUID, pageable: Pageable): Page<BuildingMeasurement> {
        return buildingMeasurementJpaRepository.findAllByBuildingId(id, pageable)
    }

    override fun getBuildingMeasurementsByFilter(
        startDate: MeasurementDate?,
        endDate: MeasurementDate?,
        types: List<MeasurementType>?,
        typeHeaders: List<MeasurementTypeHeader>?,
        pageable: Pageable
    ): Page<BuildingMeasurement> {
        TODO("Not yet implemented")

    }


}

@Repository
interface BuildingMeasurementJpaRepository : JpaRepository<BuildingMeasurement, UUID> {

    fun findAllByBuildingId(buildingId: UUID, pageable: Pageable): Page<BuildingMeasurement>
    fun findByMeasurementMeasurementType(measurementType: MeasurementType, pageable: Pageable): Page<BuildingMeasurement>
    fun findByMeasurementMeasurementTypeHeaderIn(measurementTypeHeader: List<MeasurementTypeHeader>, pageable: Pageable): Page<BuildingMeasurement>

}

class BuildingMeasurementSpecification {
    companion object {

        fun filterBuildingMeasurement(startDate: MeasurementDate?,
                                      endDate: MeasurementDate?,
                                      types: List<MeasurementType>?,
                                      typeHeaders: List<MeasurementTypeHeader>?): Specification<BuildingMeasurement> {
            return Specification { root: Root<BuildingMeasurement>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                val predicates = mutableListOf<Predicate>()

                if (types != null) {
                    if(types.isNotEmpty()){
                        types.forEach{
                            predicates
                                .add(criteriaBuilder.equal(root
                                    .get<Measurement>("measurement")
                                    .get<MeasurementType>("measurementType"), it))
                        }
                    }
                }

                if (typeHeaders != null) {
                    if(typeHeaders.isNotEmpty()){
                        typeHeaders.forEach{
                            predicates
                                .add(criteriaBuilder.equal(root
                                    .get<Measurement>("measurement")
                                    .get<MeasurementTypeHeader>("measurementTypeHeader"),it))
                        }
                    }
                }

                if(startDate != null){
                    predicates
                        .add(criteriaBuilder.equal(root
                            .get<Measurement>("measurement")
                            .get<MeasurementDate>("measurementDate"),startDate))
                }

                if(endDate != null){
                    predicates
                        .add(criteriaBuilder.equal(root
                            .get<Measurement>("measurement")
                            .get<MeasurementDate>("measurementDate"),endDate))
                }
                criteriaBuilder.and(*predicates.toTypedArray())
            }
        }
    }
}