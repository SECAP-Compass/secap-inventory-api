//package org.secapcompass.secapinventoryapi.domain.building.infrastructure
//
//import jakarta.persistence.criteria.CriteriaBuilder
//import jakarta.persistence.criteria.CriteriaQuery
//import jakarta.persistence.criteria.Predicate
//import jakarta.persistence.criteria.Root
//import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingMeasurement
//import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingMeasurementCalculation
//import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementType
//import org.secapcompass.secapinventoryapi.domain.building.core.model.MeasurementTypeHeader
//import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingMeasurementCalculationRepository
//import org.secapcompass.secapinventoryapi.domain.building.core.vo.Measurement
//import org.secapcompass.secapinventoryapi.domain.building.core.vo.MeasurementCalculation
//import org.secapcompass.secapinventoryapi.domain.building.core.vo.MeasurementDate
//import org.springframework.data.domain.Page
//import org.springframework.data.domain.Pageable
//import org.springframework.data.jpa.domain.Specification
//import org.springframework.data.jpa.repository.JpaRepository
//import org.springframework.stereotype.Repository
//import java.util.*
//
//
//
//class BuildingMeasurementCalculationPsqlRepository
//    (private val buildingMeasurementCalculationJpaRepository: BuildingMeasurementCalculationJpaRepository)
//    : IBuildingMeasurementCalculationRepository{
//
//    override fun saveBuildingMeasurementCalculation(buildingMeasurementCalculation: BuildingMeasurementCalculation): BuildingMeasurementCalculation {
//        return buildingMeasurementCalculationJpaRepository.save(buildingMeasurementCalculation)
//    }
//
//    override fun getBuildingMeasurementCalculationById(
//        id: UUID,
//        pageable: Pageable
//    ): Page<BuildingMeasurementCalculation> {
//        return buildingMeasurementCalculationJpaRepository.findAllByBuildingMeasurementCalculationsId(id,pageable)
//    }
//
//}
//
//@Repository
//interface BuildingMeasurementCalculationJpaRepository : JpaRepository<BuildingMeasurementCalculation, UUID> {
//
//    fun findAllByBuildingMeasurementCalculationsId(buildingMeasurementCalculationId: UUID, pageable: Pageable): Page<BuildingMeasurementCalculation>
//}
//
//
//class BuildingMeasurementCalculationSpecification {
//    companion object {
//
//        fun filterBuildingMeasurementCalculation(startDate: MeasurementDate?,
//                                      endDate: MeasurementDate?,
//                                      types: List<MeasurementType>?,
//                                      typeHeaders: List<MeasurementTypeHeader>?): Specification<BuildingMeasurementCalculation> {
//            return Specification { root: Root<BuildingMeasurementCalculation>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
//                val predicates = mutableListOf<Predicate>()
//
//                if (types != null) {
//                    if(types.isNotEmpty()){
//                        types.forEach{
//                            predicates
//                                .add(criteriaBuilder.equal(root
//                                    .get<MeasurementCalculation>("MeasurementCalculation")
//                                    .get<MeasurementType>("measurementType"), it))
//                        }
//                    }
//                }
//
//                if (typeHeaders != null) {
//                    if(typeHeaders.isNotEmpty()){
//                        typeHeaders.forEach{
//                            predicates
//                                .add(criteriaBuilder.equal(root
//                                    .get<MeasurementCalculation>("measurementCalculation")
//                                    .get<MeasurementTypeHeader>("measurementTypeHeader"),it))
//                        }
//                    }
//                }
//
//                if(startDate != null){
//                    predicates
//                        .add(criteriaBuilder.equal(root
//                            .get<MeasurementCalculation>("measurementCalculation")
//                            .get<MeasurementDate>("measurementDate"),startDate))
//                }
//
//                if(endDate != null){
//                    predicates
//                        .add(criteriaBuilder.equal(root
//                            .get<MeasurementCalculation>("measurementCalculation")
//                            .get<MeasurementDate>("measurementDate"),endDate))
//                }
//                criteriaBuilder.and(*predicates.toTypedArray())
//            }
//        }
//    }
//}
