package dev.sezrr.projects.patikaweatherproject.model.pollution.command;

import dev.sezrr.projects.patikaweatherproject.core.model.AirQualityComponent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public record CreateNewPollutionCommand(
        @NotBlank(message = "City id cannot be empty.")
        @NotNull(message = "City name cannot be empty.")
        String cityName,

        @NotEmpty(message = "Air Quality components cannot be empty")
        Map<AirQualityComponent, Double> airQualityComponents,

        LocalDate date
)
{
    public CreateNewPollutionCommand
    {
        if (airQualityComponents == null)
        {
            airQualityComponents = new HashMap<>();
        }
    }
}
