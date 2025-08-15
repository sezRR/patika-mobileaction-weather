package dev.sezrr.projects.patikaweatherproject.model.city.query;

public record GetCityCoordinatesQueryRequest(
        String cityName,
        String stateCode,
        String countryCode
)
{

}
