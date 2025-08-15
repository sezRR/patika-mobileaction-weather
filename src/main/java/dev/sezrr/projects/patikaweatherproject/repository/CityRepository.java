package dev.sezrr.projects.patikaweatherproject.repository;

import dev.sezrr.projects.patikaweatherproject.model.city.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<City, UUID>
{

}
