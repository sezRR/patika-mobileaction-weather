package dev.sezrr.projects.patikaweatherproject.model.pollution.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Builder
public record CreateNewPollutionCommand(
        @NotNull(message = "{validation.city.name.notnull}")
        String cityName,

        @NotEmpty(message = "{validation.airQualityComponents.notempty}")
        Map<String, String> airQualityComponents,

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
