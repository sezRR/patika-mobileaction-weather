package dev.sezrr.projects.patikaweatherproject.model.city;

import dev.sezrr.projects.patikaweatherproject.core.model.GeospatialCoordinates;
import dev.sezrr.projects.patikaweatherproject.model.city.command.CreateNewCityCommand;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("City Model Validation Tests")
class CityTest
{
    private Validator validator;

    @BeforeEach
    void setUp()
    {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Should create valid city successfully")
    void createCity_WithValidData_ShouldPass()
    {
        GeospatialCoordinates coordinates = GeospatialCoordinates.builder()
                .lat(41.0082)
                .lon(28.9784)
                .build();

        City city = City.builder()
                .name("Istanbul")
                .geospatialCoordinates(coordinates)
                .country("TR")
                .state("Istanbul")
                .build();

        Set<ConstraintViolation<City>> violations = validator.validate(city);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Should fail validation when name is null")
    void createCity_WithNullName_ShouldFailValidation()
    {
        City city = City.builder()
                .name(null)
                .geospatialCoordinates(GeospatialCoordinates.builder()
                        .lat(39.9207886)
                        .lon(32.8540482)
                        .build())
                .country("TR")
                .build();

        Set<ConstraintViolation<City>> violations = validator.validate(city);

        assertThat(violations).hasSize(2); // Not Blank, Not Null
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("City name cannot be empty.");
    }

    @Test
    @DisplayName("Should fail validation when name is blank")
    void createCity_WithBlankName_ShouldFailValidation()
    {
        City city = City.builder()
                .name("   ")
                .geospatialCoordinates(GeospatialCoordinates.builder()
                        .lat(39.9207886)
                        .lon(32.8540482)
                        .build())
                .country("TR")
                .build();

        Set<ConstraintViolation<City>> violations = validator.validate(city);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("City name cannot be empty.");
    }

    @Test
    @DisplayName("Should normalize city name correctly")
    void normalizedName_ShouldReturnFormattedName()
    {
        assertThat(City.normalizedName("istanbul")).isEqualTo("Istanbul");
        assertThat(City.normalizedName("ISTANBUL")).isEqualTo("Istanbul");
        assertThat(City.normalizedName("new york")).isEqualTo("New York");
        assertThat(City.normalizedName("LOS ANGELES")).isEqualTo("Los Angeles");
        assertThat(City.normalizedName("sÃo paulo")).isEqualTo("São Paulo");
    }

    @Test
    @DisplayName("Should handle null and blank names in normalization")
    void normalizedName_WithNullOrBlank_ShouldReturnNull()
    {
        assertThat(City.normalizedName(null)).isNull();
        assertThat(City.normalizedName("")).isNull();
        assertThat(City.normalizedName("   ")).isNull();
    }

    @Test
    @DisplayName("Should map from command correctly")
    void mapFromCommand_ShouldCreateCityFromCommand()
    {
        GeospatialCoordinates coordinates = GeospatialCoordinates.builder()
                .lat(41.0082)
                .lon(28.9784)
                .build();

        CreateNewCityCommand command = CreateNewCityCommand.builder()
                .name(AllowedCityName.ANKARA)
                .geospatialCoordinates(coordinates)
                .country("TR")
                .build();

        City city = City.Mapper.fromCommand(command);

        assertThat(city.getName()).isEqualTo("Ankara");
        assertThat(city.getCountry()).isEqualTo("TR");
        assertThat(city.getGeospatialCoordinates()).isEqualTo(coordinates);
    }

    @Test
    @DisplayName("Should map to query response correctly")
    void mapToQueryResponse_ShouldCreateResponseFromCity()
    {
        GeospatialCoordinates coordinates = GeospatialCoordinates.builder()
                .lat(41.0082)
                .lon(28.9784)
                .build();

        City city = City.builder()
                .name("Istanbul")
                .geospatialCoordinates(coordinates)
                .country("TR")
                .state("Istanbul")
                .build();

        var response = City.Mapper.toQueryResponse(city);

        assertThat(response.name()).isEqualTo("Istanbul");
        assertThat(response.country()).isEqualTo("TR");
        assertThat(response.state()).isEqualTo("Istanbul");
        assertThat(response.geospatialCoordinates()).isEqualTo(coordinates);
    }
}
