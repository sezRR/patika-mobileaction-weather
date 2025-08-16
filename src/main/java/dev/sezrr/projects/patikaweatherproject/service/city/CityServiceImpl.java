package dev.sezrr.projects.patikaweatherproject.service.city;

import dev.sezrr.projects.patikaweatherproject.model.city.City;
import dev.sezrr.projects.patikaweatherproject.model.city.command.CreateNewCityCommand;
import dev.sezrr.projects.patikaweatherproject.model.city.query.CityQueryResponse;
import dev.sezrr.projects.patikaweatherproject.repository.CityRepository;
import dev.sezrr.projects.patikaweatherproject.core.webclient.openweather.OpenWeatherApiService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService
{
    private final CityRepository cityRepository;
    private final OpenWeatherApiService openWeatherApiService;

    @Override
    public List<CityQueryResponse> getAllCities(Pageable pageable) {
        return cityRepository.findAll(pageable)
                .stream()
                .map(City.Mapper::toQueryResponse)
                .toList();
    }

    @Override
    public CityQueryResponse getCityByName(String name) {
        var normalizedName = City.normalizedName(name);
        var city = cityRepository.findCityByName(normalizedName).orElseGet(() -> createCityFromOpenWeather(normalizedName));
        return City.Mapper.toQueryResponse(city);
    }

    @Override
    public CityQueryResponse createNewCity(CreateNewCityCommand createNewCityCommand) {
        return City.Mapper.toQueryResponse(
                cityRepository.save(City.Mapper.fromCommand(createNewCityCommand))
        );
    }
    
    private City createCityFromOpenWeather(String normalizedName)
    {
        final String name = Objects.requireNonNull(normalizedName, "name is required").strip();
        if (name.isEmpty()) throw new IllegalArgumentException("City name cannot be blank");

        final Supplier<EntityNotFoundException> notFound = () -> new EntityNotFoundException("City not found for the given city name: " + name);

        var onDemandCity = openWeatherApiService.getCoordinatesByCityName(name)
                .map(org.springframework.http.ResponseEntity::getBody)
                .filter(list -> !list.isEmpty())
                .orElseThrow(notFound)
                .stream()
                .filter(city -> city.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(notFound);

        var cmd = CreateNewCityCommand.builder()
                .name(onDemandCity.name())
                .geospatialCoordinates(onDemandCity.geospatialCoordinates())
                .country(onDemandCity.country())
                .state(onDemandCity.state())
                .build();

        return cityRepository.save(City.Mapper.fromCommand(cmd));
    }
}
