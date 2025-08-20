package dev.sezrr.projects.patikaweatherproject.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sezrr.projects.patikaweatherproject.core.model.GeospatialCoordinates;
import dev.sezrr.projects.patikaweatherproject.model.city.AllowedCityName;
import dev.sezrr.projects.patikaweatherproject.model.city.command.CreateNewCityCommand;
import dev.sezrr.projects.patikaweatherproject.model.city.query.CityQueryResponse;
import dev.sezrr.projects.patikaweatherproject.service.city.CityService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CitiesController.class)
@DisplayName("Cities Controller Integration Tests")
class CitiesControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CityService cityService;

    @Autowired
    private ObjectMapper objectMapper;

    private CityQueryResponse cityResponse;
    private CreateNewCityCommand createCityCommand;

    @BeforeEach
    void setUp()
    {
        GeospatialCoordinates coordinates = GeospatialCoordinates.builder()
                .lat(39.9207886)
                .lon(32.8540482)
                .build();

        cityResponse = CityQueryResponse.builder()
                .id(UUID.randomUUID())
                .name("Ankara")
                .geospatialCoordinates(coordinates)
                .country("TR")
                .build();

        createCityCommand = CreateNewCityCommand.builder()
                .name(AllowedCityName.ANKARA)
                .geospatialCoordinates(coordinates)
                .country("TR")
                .build();
    }

    @Test
    @DisplayName("GET /v1/cities - Should return all cities with pagination")
    void getAllCities_ShouldReturnPagedCities() throws Exception
    {
        List<CityQueryResponse> cities = List.of(cityResponse);
        when(cityService.getAllCities(any(Pageable.class))).thenReturn(cities);

        mockMvc.perform(get("/v1/cities")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Ankara"))
                .andExpect(jsonPath("$.data[0].country").value("TR"));

        verify(cityService).getAllCities(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /v1/cities/{name} - Should return city by name")
    void getCityByName_ShouldReturnCity() throws Exception
    {
        String cityName = "Ankara";
        when(cityService.getCityByName(cityName)).thenReturn(cityResponse);

        mockMvc.perform(get("/v1/cities/{name}", cityName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Ankara"))
                .andExpect(jsonPath("$.data.country").value("TR"))
                .andExpect(jsonPath("$.data.lat").value(39.9207886))
                .andExpect(jsonPath("$.data.lon").value(32.8540482));

        verify(cityService).getCityByName(cityName);
    }

    @Test
    @DisplayName("GET /v1/cities/{name} - Should return 404 when city not found")
    void getCityByName_WhenNotFound_ShouldReturn404() throws Exception
    {
        String cityName = "NonExistentCity";
        when(cityService.getCityByName(cityName))
                .thenThrow(new EntityNotFoundException("City not found"));

        mockMvc.perform(get("/v1/cities/{name}", cityName))
                .andExpect(status().isNotFound());

        verify(cityService).getCityByName(cityName);
    }

    @Test
    @DisplayName("POST /v1/cities - Should create new city")
    void createNewCity_ShouldReturnCreatedCity() throws Exception
    {
        when(cityService.createNewCity(any(CreateNewCityCommand.class))).thenReturn(cityResponse);

        mockMvc.perform(post("/v1/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createCityCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Ankara"))
                .andExpect(jsonPath("$.data.country").value("TR"));

        verify(cityService).createNewCity(any(CreateNewCityCommand.class));
    }

    @Test
    @DisplayName("POST /v1/cities - Should return 400 for invalid request body")
    void createNewCity_WithInvalidData_ShouldReturn400() throws Exception
    {
        CreateNewCityCommand invalidCommand = CreateNewCityCommand.builder().build();

        mockMvc.perform(post("/v1/cities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCommand)))
                .andExpect(status().isBadRequest());

        verify(cityService, never()).createNewCity(any(CreateNewCityCommand.class));
    }

    @Test
    @DisplayName("GET /v1/cities - Should handle pagination parameters correctly")
    void getAllCities_WithCustomPagination_ShouldApplyPagination() throws Exception
    {
        List<CityQueryResponse> cities = List.of(cityResponse);
        when(cityService.getAllCities(any(Pageable.class))).thenReturn(cities);

        mockMvc.perform(get("/v1/cities")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());

        verify(cityService).getAllCities(argThat(pageable ->
                pageable.getPageNumber() == 1 &&
                pageable.getPageSize() == 10 &&
                pageable.getSort().isSorted()
        ));
    }
}
