package org.secapcompass.secapinventoryapi.configuration

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.secapcompass.secapinventoryapi.domain.building.core.vo.MeasurementCalculation


@Converter(autoApply = false)
class CalculationConverter : AttributeConverter<MeasurementCalculation, String> {

    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: MeasurementCalculation): String {
        return objectMapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String): MeasurementCalculation {
        return objectMapper.readValue(dbData, MeasurementCalculation::class.java)
    }
}
