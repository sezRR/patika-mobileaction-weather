package dev.sezrr.projects.patikaweatherproject.service.impl;

import dev.sezrr.projects.patikaweatherproject.model.city.City;
import dev.sezrr.projects.patikaweatherproject.model.city.command.CreateNewCityCommand;
import dev.sezrr.projects.patikaweatherproject.model.city.query.CityQueryResponse;
import dev.sezrr.projects.patikaweatherproject.repository.CityRepository;
import dev.sezrr.projects.patikaweatherproject.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService
{
    private final CityRepository cityRepository;

    @Override
    public CityQueryResponse createNewCity(CreateNewCityCommand createNewCityCommand) {
        return City.Mapper.toQueryResponse(
                cityRepository.save(City.Mapper.fromCommand(createNewCityCommand))
        );
    }
}
