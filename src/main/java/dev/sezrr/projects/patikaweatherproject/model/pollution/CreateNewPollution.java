package dev.sezrr.projects.patikaweatherproject.model.pollution;

import dev.sezrr.projects.patikaweatherproject.core.model.AirQualityComponent;
import dev.sezrr.projects.patikaweatherproject.core.model.DateFilterObject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record CreateNewPollution(
        @NotNull(message = "City id cannot be empty.")
        UUID cityId,

        @NotNull(message = "Air Quality components cannot be empty")
        @NotBlank(message = "Air Quality components cannot be empty")
        Map<AirQualityComponent, Double> airQualityComponents,

        DateFilterObject dateFilterObject
)
{

}
