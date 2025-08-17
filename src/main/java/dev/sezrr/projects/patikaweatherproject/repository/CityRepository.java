package dev.sezrr.projects.patikaweatherproject.repository;

import dev.sezrr.projects.patikaweatherproject.model.city.City;
import dev.sezrr.projects.patikaweatherproject.model.city.query.GetCityGeoCoordinatesQueryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<City, UUID>
{
    Optional<City> findCityByName(String name);
    Optional<GetCityGeoCoordinatesQueryResponse> findGeoCoordinatesByName(String name);
}
