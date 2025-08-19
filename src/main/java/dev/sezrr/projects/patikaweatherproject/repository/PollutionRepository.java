package dev.sezrr.projects.patikaweatherproject.repository;

import dev.sezrr.projects.patikaweatherproject.model.pollution.Pollution;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PollutionRepository extends JpaRepository<Pollution, UUID>
{
    @Query("""
        SELECT p FROM Pollution p
        WHERE p.city.id = :cityId AND p.date BETWEEN :start AND :end
    """)
    List<Pollution> findAllByCityIdAndInRange(UUID cityId, LocalDate start, LocalDate end, Pageable pageable);

    long deleteAllByCityName(String cityName);
    void deleteById(UUID id);
}
