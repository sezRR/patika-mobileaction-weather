package dev.sezrr.projects.patikaweatherproject.model.pollution.query;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.sezrr.projects.patikaweatherproject.core.model.AirQualityComponent;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record PollutionQueryResponse(
        UUID id,
        String cityName,
        Map<AirQualityComponent, Double> airQualityComponents,
        LocalDate date,
        boolean isActive
)
{

}
