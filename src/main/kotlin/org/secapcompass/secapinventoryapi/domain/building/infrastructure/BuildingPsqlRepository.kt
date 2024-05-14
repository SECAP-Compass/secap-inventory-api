package org.secapcompass.secapinventoryapi.domain.building.infrastructure

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.secapcompass.secapinventoryapi.domain.building.core.model.Building
import org.secapcompass.secapinventoryapi.domain.building.core.model.BuildingType
import org.secapcompass.secapinventoryapi.domain.building.core.repository.IBuildingRepository
import org.secapcompass.secapinventoryapi.domain.building.core.vo.Address
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID


@Repository
class BuildingPsqlRepository(private val buildingJpaRepository: BuildingJpaRepository) : IBuildingRepository{

    override fun saveBuilding(building: Building): Building {
        return buildingJpaRepository.save(building)
    }

    override fun getBuildingById(id: UUID): Optional<Building> {
        return buildingJpaRepository.findById(id)
    }

    override fun getBuildings(pageable: Pageable): Page<Building> {
        return buildingJpaRepository.findAll(pageable)
    }

    override fun getBuildingsByFilter(address: Address, buildingType: BuildingType?, pageable: Pageable): Page<Building> {
        return buildingJpaRepository.findAll(BuildingSpecification.filterBuilding(address, buildingType), pageable)
    }
}

@Repository
interface BuildingJpaRepository : JpaRepository<Building, UUID>,
    JpaSpecificationExecutor<Building>  {}

class BuildingSpecification {
    companion object {

        fun filterBuilding(address: Address, buildingType: BuildingType?): Specification<Building> {
            return Specification { root: Root<Building>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder ->
                val predicates = mutableListOf<Predicate>()

                if (address.province != null) {
                    predicates.add(criteriaBuilder.equal(root.get<Address>("address").get<String?>("province"), address.province))
                }

                if (address.district != null) {
                    predicates.add(criteriaBuilder.equal(root.get<Address>("address").get<String?>("district"), address.district))
                }

                if (buildingType != null) {
                    predicates.add(criteriaBuilder.equal(root.get<BuildingType>("type"), buildingType))
                }

                criteriaBuilder.and(*predicates.toTypedArray())
            }
        }
    }
}