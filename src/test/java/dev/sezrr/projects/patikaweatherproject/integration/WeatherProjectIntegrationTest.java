package dev.sezrr.projects.patikaweatherproject.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sezrr.projects.patikaweatherproject.config.TestContainerConfig;
import dev.sezrr.projects.patikaweatherproject.core.model.GeospatialCoordinates;
import dev.sezrr.projects.patikaweatherproject.model.city.AllowedCityName;
import dev.sezrr.projects.patikaweatherproject.model.city.City;
import dev.sezrr.projects.patikaweatherproject.model.city.command.CreateNewCityCommand;
import dev.sezrr.projects.patikaweatherproject.model.pollution.Pollution;
import dev.sezrr.projects.patikaweatherproject.model.pollution.command.CreateNewPollutionCommand;
import dev.sezrr.projects.patikaweatherproject.repository.CityRepository;
import dev.sezrr.projects.patikaweatherproject.repository.PollutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainerConfig.class)
@ActiveProfiles("test")
@AutoConfigureWebMvc
@Transactional
@DisplayName("Weather Project Integration Tests")
class WeatherProjectIntegrationTest
{
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private PollutionRepository pollutionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp()
    {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        pollutionRepository.deleteAll();
        cityRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create city and pollution records end-to-end")
    void createCityAndPollution_EndToEnd_ShouldWork() throws Exception
    {
        GeospatialCoordinates coordinates = GeospatialCoordinates.builder()
                .lat(39.9207886)
                .lon(32.8540482)
                .build();

        CreateNewCityCommand cityCommand = CreateNewCityCommand.builder()
                .name(AllowedCityName.ANKARA)
                .geospatialCoordinates(coordinates)
                .country("TR")
                .build();

        mockMvc.perform(post("/v1/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cityCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Ankara"));

        Map<String, String> airQuality = Map.of(
                "co", "233.75",
                "no2", "15.52",
                "pm2_5", "12.34"
        );

        CreateNewPollutionCommand pollutionCommand = CreateNewPollutionCommand.builder()
                .cityName("Ankara")
                .airQualityComponents(airQuality)
                .date(LocalDate.now())
                .build();

        mockMvc.perform(post("/v1/pollutions/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pollutionCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cityName").value("Ankara"))
                .andExpect(jsonPath("$.data.airQualityComponents.co").value("233.75"));

        mockMvc.perform(get("/v1/cities/Ankara"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Ankara"));

        LocalDate today = LocalDate.now();
        mockMvc.perform(get("/v1/pollutions/history")
                        .param("city", "Ankara")
                        .param("start", today.minusDays(2).toString())
                        .param("end", today.minusDays(1).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].cityName").value("Ankara"));
    }

    @Test
    @DisplayName("Should handle pagination across endpoints")
    void testPaginationAcrossEndpoints_ShouldWork() throws Exception
    {
        for (int i = 1; i <= 7; i++) {
            City city = City.builder()
                    .name("TestCity" + i)
                    .geospatialCoordinates(GeospatialCoordinates.builder()
                            .lat(40.0 + i)
                            .lon(29.0 + i)
                            .build())
                    .country("TR")
                    .state("Test")
                    .build();
            cityRepository.save(city);

            Pollution pollution = Pollution.builder()
                    .city(city)
                    .airQualityComponents(Map.of("co", "100." + i))
                    .date(LocalDate.now().minusDays(i))
                    .build();
            pollution.setActive(true);
            pollutionRepository.save(pollution);
        }

        mockMvc.perform(get("/v1/cities")
                        .param("page", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)));

        mockMvc.perform(get("/v1/cities")
                        .param("page", "1")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(3)));

        mockMvc.perform(get("/v1/pollutions/history/all")
                        .param("page", "0")
                        .param("size", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(4)));
    }

    @Test
    @DisplayName("Should handle date filtering for pollution history")
    void testDateFilteringForPollutions_ShouldWork() throws Exception
    {
        City city = City.builder()
                .name("Ankara")
                .geospatialCoordinates(GeospatialCoordinates.builder()
                        .lat(39.9207886)
                        .lon(32.8540482)
                        .build())
                .country("TR")
                .build();
        City savedCity = cityRepository.save(city);

        LocalDate today = LocalDate.now();
        for (int i = 0; i < 10; i++) {
            Pollution pollution = Pollution.builder()
                    .city(savedCity)
                    .airQualityComponents(Map.of("co", "100." + i))
                    .date(today.minusDays(i))
                    .build();
            pollution.setActive(true);
            pollutionRepository.save(pollution);
        }

        String startDate = today.minusDays(5).toString();
        String endDate = today.minusDays(1).toString();

        mockMvc.perform(get("/v1/pollutions/history")
                        .param("city", "Ankara")
                        .param("start", startDate)
                        .param("end", endDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(5)));
    }

    @Test
    @DisplayName("Should delete pollution record successfully")
    void testPollutionDeletion_ShouldWork() throws Exception
    {
        City city = City.builder()
                .name("DeleteTestCity")
                .geospatialCoordinates(GeospatialCoordinates.builder()
                        .lat(39.9207886)
                        .lon(32.8540482)
                        .build())
                .country("TR")
                .state("Test")
                .build();
        City savedCity = cityRepository.save(city);

        Pollution pollution = Pollution.builder()
                .city(savedCity)
                .airQualityComponents(Map.of("co", "100.0"))
                .date(LocalDate.now())
                .build();
        pollution.setActive(true);
        Pollution savedPollution = pollutionRepository.save(pollution);

        mockMvc.perform(delete("/v1/pollutions/history/{id}", savedPollution.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/pollutions/history")
                        .param("city", "DeleteTestCity")
                        .param("startDate", LocalDate.now().minusDays(1).toString())
                        .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should handle sorting correctly")
    void testSorting_ShouldWork() throws Exception
    {
        String[] cityNames = {"Ankara", "Mumbai", "London", "Barcelona"};

        for (String name : cityNames) {
            City city = City.builder()
                    .name(name)
                    .geospatialCoordinates(GeospatialCoordinates.builder()
                            .lat(39.9)
                            .lon(32.8)
                            .build())
                    .country("TR")
                    .state("Test")
                    .build();
            cityRepository.save(city);
        }

        mockMvc.perform(get("/v1/cities")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Ankara"))
                .andExpect(jsonPath("$.data[1].name").value("Barcelona"));

        mockMvc.perform(get("/v1/cities")
                        .param("sort", "name,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Mumbai"))
                .andExpect(jsonPath("$.data[1].name").value("London"));
    }
}
