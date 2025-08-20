package dev.sezrr.projects.patikaweatherproject.model.city.command;

import dev.sezrr.projects.patikaweatherproject.core.model.GeospatialCoordinates;
import dev.sezrr.projects.patikaweatherproject.model.city.AllowedCityName;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreateNewCityCommand(
        @NotNull(message = "{validation.city.name.notnull}")
        AllowedCityName name,

        GeospatialCoordinates geospatialCoordinates,

        String country,
        String state
)
{

}
