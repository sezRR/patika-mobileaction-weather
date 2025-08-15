package dev.sezrr.projects.patikaweatherproject.model.city.command;

import dev.sezrr.projects.patikaweatherproject.core.model.GeospatialCoordinates;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateNewCityCommand(
        @NotBlank(message = "City name cannot be empty.")
        @NotNull(message = "City name cannot be empty.")
        String name,

        GeospatialCoordinates geospatialCoordinates,

        String country,
        String state
) {
}
