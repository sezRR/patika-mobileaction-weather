package dev.sezrr.projects.patikaweatherproject.service.pollution;

import dev.sezrr.projects.patikaweatherproject.core.model.DateFilterObject;
import dev.sezrr.projects.patikaweatherproject.model.pollution.command.CreateNewPollutionCommand;
import dev.sezrr.projects.patikaweatherproject.model.pollution.query.PollutionQueryResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PollutionService
{
    List<PollutionQueryResponse> getAllPollutions(Pageable pageable);
    List<PollutionQueryResponse> getPollutionsByCityNameInRange(String cityName, DateFilterObject dateFilterObject, Pageable pageable);
    PollutionQueryResponse createNewPollution(CreateNewPollutionCommand createNewPollutionCommand);
    void deletePollutionById(UUID id);
    void deletePollutionByCityName(String cityName);
}
