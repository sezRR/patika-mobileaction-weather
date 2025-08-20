package dev.sezrr.projects.patikaweatherproject.model.city.query;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import dev.sezrr.projects.patikaweatherproject.core.model.GeospatialCoordinates;

import java.util.UUID;

public record GetCityGeoCoordinatesQueryResponse(
        UUID id,
        String name,
        @JsonUnwrapped GeospatialCoordinates geospatialCoordinates
)
{

}
