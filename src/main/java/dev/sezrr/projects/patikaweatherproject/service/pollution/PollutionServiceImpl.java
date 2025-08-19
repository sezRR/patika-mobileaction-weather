package dev.sezrr.projects.patikaweatherproject.service.pollution;

import dev.sezrr.projects.patikaweatherproject.core.model.DateFilterObject;
import dev.sezrr.projects.patikaweatherproject.core.util.DateHelper;
import dev.sezrr.projects.patikaweatherproject.core.validation.CustomValidatorHelper;
import dev.sezrr.projects.patikaweatherproject.infrastructure.webclient.openweather.OpenWeatherApiService;
import dev.sezrr.projects.patikaweatherproject.infrastructure.webclient.openweather.model.OWAPollutionHistory;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.AQIAveragingService;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AirQualityComponent;
import dev.sezrr.projects.patikaweatherproject.model.city.City;
import dev.sezrr.projects.patikaweatherproject.model.city.query.GetCityGeoCoordinatesQueryResponse;
import dev.sezrr.projects.patikaweatherproject.model.pollution.Pollution;
import dev.sezrr.projects.patikaweatherproject.model.pollution.command.CreateNewPollutionCommand;
import dev.sezrr.projects.patikaweatherproject.model.pollution.query.GetCityPollutionQueryRequest;
import dev.sezrr.projects.patikaweatherproject.model.pollution.query.PollutionQueryResponse;
import dev.sezrr.projects.patikaweatherproject.repository.CityRepository;
import dev.sezrr.projects.patikaweatherproject.repository.PollutionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PollutionServiceImpl implements PollutionService
{
    private final OpenWeatherApiService openWeatherApiService;
    private final AQIAveragingService aqiAveragingService;
    private final PollutionRepository pollutionRepository;
    private final CityRepository cityRepository;
    private final CustomValidatorHelper customValidatorHelper;

    @Override
    public List<PollutionQueryResponse> getAllPollutions(Pageable pageable) {
        return pollutionRepository.findAll(pageable)
                .stream()
                .map(Pollution.Mapper::toQueryResponse)
                .toList();
    }

    @Override
    public List<PollutionQueryResponse> getPollutionsByCityNameInRange(String cityName, DateFilterObject dateFilterObject, Pageable pageable) {
        // Validation and business logic
        if (cityName == null || cityName.isBlank())
        {
            throw new IllegalArgumentException("City name cannot be null or empty.");
        }

        var normalizedCityName = City.normalizedName(cityName);
        var cityGeoCoordinates = cityRepository.findGeoCoordinatesByName(normalizedCityName)
                .orElseThrow(() -> new EntityNotFoundException("City not found: " + normalizedCityName));

        dateFilterObject = DateFilterObject.normalizeMissingDateFilterObject(dateFilterObject);
        customValidatorHelper.validateOrThrow(dateFilterObject, "Invalid date range provided.");

        // Fetch pollution data
        var pollutions = new ArrayList<>(pollutionRepository.findAllByCityIdAndInRange(
                        cityGeoCoordinates.id(),
                        dateFilterObject.getStart(),
                        dateFilterObject.getEnd(),
                        pageable
                ).stream()
                .map(Pollution.Mapper::toQueryResponse)
                .toList());

        // Adjust date range within the pagination limits
        var adjustedDateRange = DateHelper.adjustRangeWithinPagination(dateFilterObject, pageable);
        if (adjustedDateRange == null)
            return List.of();

        // Check if we have enough data for the requested range
        var expectedDays = Math.min(ChronoUnit.DAYS.between(dateFilterObject.getStart(), dateFilterObject.getEnd()) + 1, pageable.getPageSize());
        if (pollutions.size() >= expectedDays) {
            return pollutions;
        }

        // Calculate missing dates
        var missingDates = DateHelper.findMissingDatesInRange(
                dateFilterObject,
                pollutions.stream()
                        .map(PollutionQueryResponse::date)
                        .toList()
        );

        // Calculate ranges for missing dates (it is for optimizing on-demand OWA API calls)
        var ranges = DateHelper.generateIntervals(missingDates);
        var newPollutionCmds = fetchPollutionHistoryWithinRanges(ranges, cityGeoCoordinates);

        var newPollutions = newPollutionCmds.stream().map(cmd -> Pollution.Mapper.fromCommand(cmd, City.Mapper.fromQueryResponse(cityGeoCoordinates))).toList();
        var savedPollutions = pollutionRepository.saveAll(newPollutions);
        savedPollutions.sort(new Pollution.Sort.SortByDate());

        pollutions.addAll(savedPollutions.stream()
                .map(Pollution.Mapper::toQueryResponse)
                .toList());

        log.warn("Not all days have pollution data for city {} in the range {} to {}. Found: {}",
                cityName, dateFilterObject.getStart(), dateFilterObject.getEnd(), pollutions.size());

        return pollutions;
    }

    private ArrayList<CreateNewPollutionCommand> fetchPollutionHistoryWithinRanges(List<DateFilterObject> ranges, GetCityGeoCoordinatesQueryResponse cityGeoCoordinates)
    {
        var newPollutionCmds = new ArrayList<CreateNewPollutionCommand>();
        for (var range : ranges) {
            log.warn("Missing pollution data for city {} on dates: {} to {}",
                    cityGeoCoordinates.name(), range.getStart(), range.getEnd());

            var getCityPollutionQueryRequest = GetCityPollutionQueryRequest.builder()
                    .geospatialCoordinates(cityGeoCoordinates.geospatialCoordinates())
                    .dateFilterObject(DateFilterObject.builder()
                            .start(range.getStart())
                            .end(range.getEnd())
                            .build())
                    .build();

            var pollutionHistory = openWeatherApiService.getPollutionHistoryByCityName(getCityPollutionQueryRequest);
            if (pollutionHistory.isEmpty()) {
                log.error("Failed to fetch pollution data for city {} in the range {} to {}.",
                        cityGeoCoordinates.name(), range.getStart(), range.getEnd());
                continue;
            }

            classifyComponentsAndCreateNewPollution(cityGeoCoordinates.name(), range, pollutionHistory, newPollutionCmds);
        }
        return newPollutionCmds;
    }

    private void classifyComponentsAndCreateNewPollution(String cityName, DateFilterObject range, Optional<OWAPollutionHistory.QueryResponse> pollutionHistory, ArrayList<CreateNewPollutionCommand> newPollutionCmds) {
        var pollutionHistoryData = pollutionHistory.orElseThrow(
                () -> new IllegalArgumentException("Pollution history data is empty for city: " + cityName)
        ).getList();

        if (pollutionHistoryData.isEmpty()) {
            log.warn("No pollution data found for city {} in the range {} to {}.", cityName, range.getStart(), range.getEnd());
            return;
        }

        // Data is hourly, so we expect 24 entries per day
        var days = pollutionHistoryData.size() / 24;
        log.info("{} days", pollutionHistoryData.size() / 24);

        for (int i = 0; i < days; i++) {
            var startIndex = i * 24;
            var endIndex = startIndex + 24;

            var dailyData = pollutionHistoryData.subList(startIndex, endIndex);
            if (dailyData.isEmpty()) {
                log.warn("No pollution data found for city {} on day {}", cityName, range.getStart().plusDays(i));
                continue;
            }

            var aqiComponentsWithValues = Map.of(
                    AirQualityComponent.CO, dailyData.stream().map(p -> p.getComponents().getCo()).toList(),
                    AirQualityComponent.O3, dailyData.stream().map(p -> p.getComponents().getO3()).toList(),
                    AirQualityComponent.SO2, dailyData.stream().map(p -> p.getComponents().getSo2()).toList()
            );

            // Calculate averaging periods and categorize components, strategy pattern is used inside the below method
            var categorizedComponents = aqiAveragingService.calculateAveragingPeriodsAndCategorizeComponents(aqiComponentsWithValues);

            var createNewPollutionCommand = CreateNewPollutionCommand.builder()
                    .cityName(cityName)
                    .date(range.getStart().plusDays(i))
                    .airQualityComponents(Map.of(
                            AirQualityComponent.CO.name(), categorizedComponents.get(AirQualityComponent.CO),
                            AirQualityComponent.O3.name(), categorizedComponents.get(AirQualityComponent.O3),
                            AirQualityComponent.SO2.name(), categorizedComponents.get(AirQualityComponent.SO2)
                    ))
                    .build();
            newPollutionCmds.add(createNewPollutionCommand);
        }
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

    @Override
    public void deletePollutionById(UUID id) {
        pollutionRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deletePollutionByCityName(String cityName) {
        pollutionRepository.deleteAllByCityName(City.normalizedName(cityName));
    }
}
