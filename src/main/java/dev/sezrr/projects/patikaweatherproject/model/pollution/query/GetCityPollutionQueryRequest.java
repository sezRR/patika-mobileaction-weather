package dev.sezrr.projects.patikaweatherproject.model.pollution.query;

import dev.sezrr.projects.patikaweatherproject.core.model.DateFilterObject;
import dev.sezrr.projects.patikaweatherproject.core.model.GeospatialCoordinates;
import lombok.Builder;

@Builder
public record GetCityPollutionQueryRequest(
        DateFilterObject dateFilterObject,
        GeospatialCoordinates geospatialCoordinates
)
{

}
