package dev.sezrr.projects.patikaweatherproject.service.pollution;

import dev.sezrr.projects.patikaweatherproject.core.model.DateFilterObject;
import dev.sezrr.projects.patikaweatherproject.core.util.DateHelper;
import dev.sezrr.projects.patikaweatherproject.core.webclient.openweather.OpenWeatherApiService;
import dev.sezrr.projects.patikaweatherproject.model.city.City;
import dev.sezrr.projects.patikaweatherproject.model.pollution.Pollution;
import dev.sezrr.projects.patikaweatherproject.model.pollution.command.CreateNewPollutionCommand;
import dev.sezrr.projects.patikaweatherproject.model.pollution.query.getcitypolllution.GetCityPollutionQueryRequest;
import dev.sezrr.projects.patikaweatherproject.model.pollution.query.PollutionQueryResponse;
import dev.sezrr.projects.patikaweatherproject.repository.CityRepository;
import dev.sezrr.projects.patikaweatherproject.repository.PollutionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PollutionServiceImpl implements PollutionService
{
    private final OpenWeatherApiService openWeatherApiService;
    private final PollutionRepository pollutionRepository;
    private final CityRepository cityRepository;

    @Override
    public List<PollutionQueryResponse> getAllPollutions(Pageable pageable) {
        return pollutionRepository.findAll(pageable)
                .stream()
                .map(Pollution.Mapper::toQueryResponse)
                .toList();
    }

    @Override
    public List<PollutionQueryResponse> getPollutionsByCityNameInRange(String cityName, DateFilterObject dateFilterObject, Pageable pageable) {
        if (cityName == null || cityName.isBlank())
        {
            throw new IllegalArgumentException("City name cannot be null or empty.");
        }

        var normalizedCityName = City.normalizedName(cityName);
        var cityGeoCoordinates = cityRepository.findGeoCoordinatesByName(normalizedCityName)
                .orElseThrow(() -> new EntityNotFoundException("City not found: " + normalizedCityName));

        if (dateFilterObject == null || !dateFilterObject.isValidRange())
        {
            dateFilterObject = DateFilterObject.builder()
                    .start(LocalDate.now().minusWeeks(1)) // Default to 1 year ago
                    .end(LocalDate.now()) // Default to now
                    .build();

            log.warn("Invalid or missing date range for city {}. Defaulting to {}..{}.",
                    cityName, dateFilterObject.getStart(), dateFilterObject.getEnd());
        }

        var pollutions = new java.util.ArrayList<>(pollutionRepository.findAllByCityNameAndInRange(
                        cityName,
                        dateFilterObject.getStart(),
                        dateFilterObject.getEnd(),
                        pageable
                ).stream()
                .map(Pollution.Mapper::toQueryResponse)
                .toList());

        var expectedDays = ChronoUnit.DAYS.between(dateFilterObject.getStart(), dateFilterObject.getEnd()) + 1;
        if (pollutions.size() < expectedDays) {
            var missingDates = DateHelper.findMissingDatesInRange(
                    dateFilterObject,
                    pollutions.stream()
                            .map(PollutionQueryResponse::date)
                            .toList()
            );

            var ranges = DateHelper.generateIntervals(missingDates);

            for (var range : ranges) {
                log.warn("Missing pollution data for city {} on dates: {} to {}",
                        cityName, range.getStart(), range.getEnd());

                var getCityPollutionQueryRequest = GetCityPollutionQueryRequest.builder()
                        .geospatialCoordinates(cityGeoCoordinates.geospatialCoordinates())
                        .dateFilterObject(DateFilterObject.builder()
                                .start(range.getStart())
                                .end(range.getEnd())
                                .build())
                        .build();

                // TODO: Remove, this block is just for debug purposes
                var pollutionHistory = openWeatherApiService.getPollutionHistoryByCityName(getCityPollutionQueryRequest);
                if (pollutionHistory.isEmpty()) {
                    log.error("Failed to fetch pollution data for city {} in the range {} to {}.",
                            cityName, range.getStart(), range.getEnd());
                    continue;
                }

                var pollutionHistoryData = pollutionHistory.get().getList();
                // TODO: CALCULATE AVG POLLUTIONS
            }

            log.warn("Not all days have pollution data for city {} in the range {} to {}. Found: {}",
                    cityName, dateFilterObject.getStart(), dateFilterObject.getEnd(), pollutions.size());
        }

        return pollutions;
    }

    @Override
    public PollutionQueryResponse createNewPollution(CreateNewPollutionCommand createNewPollutionCommand) {
        var city = cityRepository.findCityByName(createNewPollutionCommand.cityName()).orElseThrow(
                () -> new IllegalArgumentException("City not found: " + createNewPollutionCommand.cityName())
        );

        return Pollution.Mapper.toQueryResponse(
                pollutionRepository.save(Pollution.Mapper.fromCommand(createNewPollutionCommand, city))
        );
    }
}
