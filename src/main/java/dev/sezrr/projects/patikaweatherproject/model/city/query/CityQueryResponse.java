package dev.sezrr.projects.patikaweatherproject.model.city.query;

import dev.sezrr.projects.patikaweatherproject.core.model.GeospatialCoordinates;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CityQueryResponse(
        UUID id,
        String name,
        GeospatialCoordinates geospatialCoordinates,
        String country,
        String state
)
{

}
