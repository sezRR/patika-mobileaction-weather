package dev.sezrr.projects.patikaweatherproject.model.city.query;

// TODO: to be able to handle "q={city name},{state code},{country code}"
public record GetCityCoordinatesQueryRequest(
        String cityName,
        String stateCode,
        String countryCode
)
{

}
