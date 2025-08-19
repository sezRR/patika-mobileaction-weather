package dev.sezrr.projects.patikaweatherproject.service.pollution;

import dev.sezrr.projects.patikaweatherproject.core.model.DateFilterObject;
import dev.sezrr.projects.patikaweatherproject.core.util.DateHelper;
import dev.sezrr.projects.patikaweatherproject.core.webclient.openweather.OpenWeatherApiService;
import dev.sezrr.projects.patikaweatherproject.core.webclient.openweather.model.OWAPollutionHistory;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.AQIAveragingService;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AirQualityComponent;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.strategy.co.AQICOCategorizerStrategy;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.strategy.o3.AQIO3CategorizerStrategy;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.strategy.so2.AQISO2CategorizerStrategy;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PollutionServiceImpl implements PollutionService
{
    private final OpenWeatherApiService openWeatherApiService;
    private final AQIAveragingService aqiAveragingService;
    private final PollutionRepository pollutionRepository;
    private final CityRepository cityRepository;

    @Override
    public List<PollutionQueryResponse> getAllPollutions(Pageable pageable) {
        return pollutionRepository.findAll(pageable)
                .stream()
                .map(Pollution.Mapper::toQueryResponse)
                .toList();
    }

    // TODO: REFACTOR
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

        var pollutions = new ArrayList<>(pollutionRepository.findAllByCityNameAndInRange(
                        normalizedCityName,
                        dateFilterObject.getStart(),
                        dateFilterObject.getEnd(),
                        pageable
                ).stream()
                .map(Pollution.Mapper::toQueryResponse)
                .toList());

//        if (pageable.getPageSize() < days)
//        {
//            dateFilterObject.setEnd(dateFilterObject.getEnd().minusDays(pageable.getOffset()));
//            dateFilterObject.setStart(dateFilterObject.getEnd().minusDays(Math.max(0, pageable.getPageSize() - 1)));
//        }
        if (pageable.getOffset() > 0)
        {
            dateFilterObject.setEnd(dateFilterObject.getEnd().minusDays(pageable.getOffset()));

            if (dateFilterObject.getStart().isAfter(dateFilterObject.getEnd())) {
                return List.of();
            }
        }

        var days = ChronoUnit.DAYS.between(dateFilterObject.getStart(), dateFilterObject.getEnd()) + 1;

        if (days > pageable.getPageSize())
        {
            dateFilterObject.setStart(dateFilterObject.getEnd().plusDays(Math.max(pageable.getPageSize() - 1, 0)));
            log.warn("Date range for city {} is too large. Adjusting to {}..{}.",
                    cityName, dateFilterObject.getStart(), dateFilterObject.getEnd());
        }

        var expectedDays = Math.min(ChronoUnit.DAYS.between(dateFilterObject.getStart(), dateFilterObject.getEnd()) + 1, pageable.getPageSize());
        if (pollutions.size() < expectedDays) {
            var missingDates = DateHelper.findMissingDatesInRange(
                    dateFilterObject,
                    pollutions.stream()
                            .map(PollutionQueryResponse::date)
                            .toList()
            );

            var ranges = DateHelper.generateIntervals(missingDates);

            var newPollutionCmds = new ArrayList<CreateNewPollutionCommand>();
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

                var pollutionHistory = openWeatherApiService.getPollutionHistoryByCityName(getCityPollutionQueryRequest);
                if (pollutionHistory.isEmpty()) {
                    log.error("Failed to fetch pollution data for city {} in the range {} to {}.",
                            cityName, range.getStart(), range.getEnd());
                    continue;
                }

                classifyComponentsAndCreateNewPollution(cityName, range, pollutionHistory, newPollutionCmds);
            }

            var newPollutions = newPollutionCmds.stream().map(cmd -> Pollution.Mapper.fromCommand(cmd, City.Mapper.fromQueryResponse(cityGeoCoordinates))).toList();
            var savedPollutions = pollutionRepository.saveAll(newPollutions);
            savedPollutions.sort(new Pollution.Sort.SortByDate());

            pollutions.addAll(savedPollutions.stream()
                    .map(Pollution.Mapper::toQueryResponse)
                    .toList());

            log.warn("Not all days have pollution data for city {} in the range {} to {}. Found: {}",
                    cityName, dateFilterObject.getStart(), dateFilterObject.getEnd(), pollutions.size());
        }

        return pollutions;
    }

    private void classifyComponentsAndCreateNewPollution(String cityName, DateFilterObject range, Optional<OWAPollutionHistory.QueryResponse> pollutionHistory, ArrayList<CreateNewPollutionCommand> newPollutionCmds) {
        var pollutionHistoryData = pollutionHistory.get().getList();
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

            final List<Double> coValues = dailyData.stream()
                    .map(p -> p.getComponents().getCo())
                    .toList();

            final List<Double> o3Values = dailyData.stream()
                    .map(p -> p.getComponents().getO3())
                    .toList();

            final List<Double> so2Values = dailyData.stream()
                    .map(p -> p.getComponents().getSo2())
                    .toList();

            var categoryCO = aqiAveragingService.calculateAveragingPeriodAndCategorizeComponent(
                    new AQICOCategorizerStrategy(),
                    coValues
            );
            log.info("Calculated CO category: {}", categoryCO);

            var categoryO3 = aqiAveragingService.calculateAveragingPeriodAndCategorizeComponent(
                    new AQIO3CategorizerStrategy(),
                    o3Values
            );
            log.info("Calculated O3 category: {}", categoryO3);

            var categorySO2 = aqiAveragingService.calculateAveragingPeriodAndCategorizeComponent(
                    new AQISO2CategorizerStrategy(),
                    so2Values
            );
            log.info("Calculated SO2 category: {}", categorySO2);

            var createNewPollutionCommand = CreateNewPollutionCommand.builder()
                    .cityName(cityName)
                    .date(range.getStart().plusDays(i))
                    .airQualityComponents(Map.of(
                            AirQualityComponent.CO.name(), categoryCO.getCategory(),
                            AirQualityComponent.O3.name(), categoryO3.getCategory(),
                            AirQualityComponent.SO2.name(), categorySO2.getCategory()
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
}
