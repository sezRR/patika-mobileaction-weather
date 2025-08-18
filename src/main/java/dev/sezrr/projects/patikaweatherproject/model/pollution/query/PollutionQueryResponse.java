package dev.sezrr.projects.patikaweatherproject.model.pollution.query;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record PollutionQueryResponse(
        UUID id,
        String cityName,
        Map<String, String> airQualityComponents,
        LocalDate date,
        boolean isActive
)
{

}
