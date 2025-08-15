package dev.sezrr.projects.patikaweatherproject.repository;

import dev.sezrr.projects.patikaweatherproject.model.pollution.Pollution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PollutionRepository extends JpaRepository<Pollution, UUID>
{

}
