package dev.sezrr.projects.patikaweatherproject.service.city;

import dev.sezrr.projects.patikaweatherproject.model.city.City;
import dev.sezrr.projects.patikaweatherproject.model.city.command.CreateNewCityCommand;
import dev.sezrr.projects.patikaweatherproject.model.city.query.CityQueryResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CityService
{
    List<CityQueryResponse> getAllCities(Pageable pageable);
    CityQueryResponse getCityByName(String name);
    CityQueryResponse createNewCity(CreateNewCityCommand createNewCityCommand);
}
