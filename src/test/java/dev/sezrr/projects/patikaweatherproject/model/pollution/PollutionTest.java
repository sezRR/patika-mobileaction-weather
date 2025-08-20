package dev.sezrr.projects.patikaweatherproject.model.pollution;

import dev.sezrr.projects.patikaweatherproject.core.model.GeospatialCoordinates;
import dev.sezrr.projects.patikaweatherproject.model.city.City;
import dev.sezrr.projects.patikaweatherproject.model.pollution.command.CreateNewPollutionCommand;
import dev.sezrr.projects.patikaweatherproject.model.pollution.query.PollutionQueryResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Pollution Model Validation Tests")
class PollutionTest
{
    private Validator validator;
    private City testCity;
    private Map<String, String> airQualityComponents;

    @BeforeEach
    void setUp()
    {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        testCity = City.builder()
                .name("Ankara")
                .geospatialCoordinates(GeospatialCoordinates.builder()
                        .lat(39.9207886)
                        .lon(32.8540482)
                        .build())
                .country("TR")
                .build();

        airQualityComponents = Map.of(
                "co", "233.75",
                "no", "0.28",
                "no2", "15.52",
                "o3", "91.31",
                "so2", "5.73",
                "pm2_5", "12.34",
                "pm10", "15.67",
                "nh3", "2.45"
        );
    }

    @Test
    @DisplayName("Should create valid pollution successfully")
    void createPollution_WithValidData_ShouldPass()
    {
        Pollution pollution = Pollution.builder()
                .city(testCity)
                .airQualityComponents(airQualityComponents)
                .date(LocalDate.now())
                .build();
        pollution.setActive(true);

        Set<ConstraintViolation<Pollution>> violations = validator.validate(pollution);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should map from command correctly")
    void mapFromCommand_ShouldCreatePollutionFromCommand()
    {
        CreateNewPollutionCommand command = CreateNewPollutionCommand.builder()
                .cityName("Ankara")
                .airQualityComponents(airQualityComponents)
                .date(LocalDate.now())
                .build();

        Pollution pollution = Pollution.Mapper.fromCommand(command, testCity);

        assertThat(pollution.getCity()).isEqualTo(testCity);
        assertThat(pollution.getAirQualityComponents()).isEqualTo(airQualityComponents);
        assertThat(pollution.getDate()).isEqualTo(LocalDate.now());
        assertThat(pollution.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should map to query response correctly")
    void mapToQueryResponse_ShouldCreateResponseFromPollution()
    {
        Pollution pollution = Pollution.builder()
                .city(testCity)
                .airQualityComponents(airQualityComponents)
                .date(LocalDate.now())
                .build();
        pollution.setActive(true);

        var response = Pollution.Mapper.toQueryResponse(pollution);

        assertThat(response.cityName()).isEqualTo("Ankara");
        assertThat(response.airQualityComponents()).isEqualTo(airQualityComponents);
        assertThat(response.date()).isEqualTo(LocalDate.now());
        assertThat(response.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should handle empty air quality components")
    void createPollution_WithEmptyComponents_ShouldPass()
    {
        Pollution pollution = Pollution.builder()
                .city(testCity)
                .airQualityComponents(Map.of())
                .date(LocalDate.now())
                .build();
        pollution.setActive(true);

        Set<ConstraintViolation<Pollution>> violations = validator.validate(pollution);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should sort pollutions by date correctly")
    void sortByDate_ShouldOrderPollutionsCorrectly()
    {
        Pollution older = Pollution.builder()
                .city(testCity)
                .airQualityComponents(airQualityComponents)
                .date(LocalDate.now().minusDays(5))
                .build();

        Pollution newer = Pollution.builder()
                .city(testCity)
                .airQualityComponents(airQualityComponents)
                .date(LocalDate.now())
                .build();

        Pollution middle = Pollution.builder()
                .city(testCity)
                .airQualityComponents(airQualityComponents)
                .date(LocalDate.now().minusDays(2))
                .build();

        var comparator = new Pollution.Sort.SortByDate();

        assertThat(comparator.compare(older, newer)).isGreaterThan(0);
        assertThat(comparator.compare(newer, older)).isLessThan(0);
        assertThat(comparator.compare(older, middle)).isGreaterThan(0);
        assertThat(comparator.compare(middle, newer)).isGreaterThan(0);
        assertThat(comparator.compare(older, older)).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle null values in air quality components")
    void createPollution_WithNullComponents_ShouldPass()
    {
        Pollution pollution = Pollution.builder()
                .city(testCity)
                .airQualityComponents(null)
                .date(LocalDate.now())
                .build();
        pollution.setActive(true);

        Set<ConstraintViolation<Pollution>> violations = validator.validate(pollution);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should map from query response correctly")
    void mapFromQueryResponse_ShouldCreatePollutionFromResponse()
    {
        var queryResponse = PollutionQueryResponse.builder()
                .cityName("Ankara")
                .airQualityComponents(airQualityComponents)
                .date(LocalDate.now())
                .isActive(true)
                .build();

        Pollution pollution = Pollution.Mapper.fromQueryResponse(queryResponse, testCity);

        assertThat(pollution.getCity()).isEqualTo(testCity);
        assertThat(pollution.getAirQualityComponents()).isEqualTo(airQualityComponents);
        assertThat(pollution.getDate()).isEqualTo(LocalDate.now());
    }
}
