package dev.sezrr.projects.patikaweatherproject.service;

import dev.sezrr.projects.patikaweatherproject.model.city.command.CreateNewCityCommand;
import dev.sezrr.projects.patikaweatherproject.model.city.query.CityQueryResponse;

public interface CityService
{
    CityQueryResponse createNewCity(CreateNewCityCommand createNewCityCommand);
}
