package dev.sezrr.projects.patikaweatherproject.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sezrr.projects.patikaweatherproject.core.model.DateFilterObject;
import dev.sezrr.projects.patikaweatherproject.model.pollution.command.CreateNewPollutionCommand;
import dev.sezrr.projects.patikaweatherproject.model.pollution.query.PollutionQueryResponse;
import dev.sezrr.projects.patikaweatherproject.service.pollution.PollutionService;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PollutionsController.class)
@DisplayName("Pollutions Controller Integration Tests")
class PollutionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PollutionService pollutionService;

    @Autowired
    private ObjectMapper objectMapper;

    private PollutionQueryResponse pollutionResponse;
    private CreateNewPollutionCommand createPollutionCommand;

    @BeforeEach
    void setUp()
    {
        Map<String, String> airQualityComponents = Map.of(
                "co", "233.75",
                "no", "0.28",
                "no2", "15.52",
                "o3", "91.31",
                "so2", "5.73",
                "pm2_5", "12.34",
                "pm10", "15.67",
                "nh3", "2.45"
        );

        pollutionResponse = PollutionQueryResponse.builder()
                .id(UUID.randomUUID())
                .cityName("Ankara")
                .airQualityComponents(airQualityComponents)
                .date(LocalDate.now())
                .isActive(true)
                .build();

        createPollutionCommand = CreateNewPollutionCommand.builder()
                .cityName("Ankara")
                .airQualityComponents(airQualityComponents)
                .date(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("GET /v1/pollutions/history/all - Should return all pollutions with pagination")
    void getAllPollutions_ShouldReturnPagedPollutions() throws Exception
    {
        List<PollutionQueryResponse> pollutions = List.of(pollutionResponse);
        when(pollutionService.getAllPollutions(any(Pageable.class))).thenReturn(pollutions);

        mockMvc.perform(get("/v1/pollutions/history/all")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].cityName").value("Ankara"))
                .andExpect(jsonPath("$.data[0].airQualityComponents.co").value("233.75"));

        verify(pollutionService).getAllPollutions(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /v1/pollutions/history - Should return pollution history by city name")
    void getPollutionHistoryByCityName_ShouldReturnFilteredPollutions() throws Exception
    {
        String cityName = "Ankara";
        List<PollutionQueryResponse> pollutions = List.of(pollutionResponse);
        when(pollutionService.getPollutionsByCityNameInRange(
                eq(cityName), any(DateFilterObject.class), any(Pageable.class)))
                .thenReturn(pollutions);

        mockMvc.perform(get("/v1/pollutions/history")
                        .param("city", cityName)
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-12-31")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].cityName").value("Ankara"));

        verify(pollutionService).getPollutionsByCityNameInRange(
                eq(cityName), any(DateFilterObject.class), any(Pageable.class));
    }

    @Test
    @DisplayName("POST /v1/pollutions/history - Should create new pollution record")
    void createNewPollution_ShouldReturnCreatedPollution() throws Exception
    {
        when(pollutionService.createNewPollution(any(CreateNewPollutionCommand.class)))
                .thenReturn(pollutionResponse);

        mockMvc.perform(post("/v1/pollutions/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPollutionCommand)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.cityName").value("Ankara"))
                .andExpect(jsonPath("$.data.airQualityComponents.co").value("233.75"));

        verify(pollutionService).createNewPollution(any(CreateNewPollutionCommand.class));
    }

    @Test
    @DisplayName("DELETE /v1/pollutions/history/{id} - Should delete pollution by id")
    void deletePollutionById_ShouldReturnNoContent() throws Exception
    {
        UUID pollutionId = UUID.randomUUID();
        doNothing().when(pollutionService).deletePollutionById(pollutionId);

        mockMvc.perform(delete("/v1/pollutions/history/{id}", pollutionId))
                .andExpect(status().isNoContent());

        verify(pollutionService).deletePollutionById(pollutionId);
    }

    @Test
    @DisplayName("DELETE /v1/pollutions/history/{id} - Should return 404 when pollution not found")
    void deletePollutionById_WhenNotFound_ShouldReturn404() throws Exception
    {
        UUID pollutionId = UUID.randomUUID();
        doThrow(new EntityNotFoundException("Pollution not found"))
                .when(pollutionService).deletePollutionById(pollutionId);

        mockMvc.perform(delete("/v1/pollutions/history/{id}", pollutionId))
                .andExpect(status().isNotFound());

        verify(pollutionService).deletePollutionById(pollutionId);
    }

    @Test
    @DisplayName("POST /v1/pollutions/history - Should return 400 for invalid request body")
    void createNewPollution_WithInvalidData_ShouldReturn400() throws Exception
    {
        CreateNewPollutionCommand invalidCommand = CreateNewPollutionCommand.builder().build();

        mockMvc.perform(post("/v1/pollutions/history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCommand)))
                .andExpect(status().isBadRequest());

        verify(pollutionService, never()).createNewPollution(any(CreateNewPollutionCommand.class));
    }

    @Test
    @DisplayName("GET /v1/pollutions/history - Should handle missing city parameter")
    void getPollutionHistoryByCityName_WithoutCityParam_ShouldReturn400() throws Exception
    {
        mockMvc.perform(get("/v1/pollutions/history"))
                .andExpect(status().isBadRequest());

        verify(pollutionService, never()).getPollutionsByCityNameInRange(
                anyString(), any(DateFilterObject.class), any(Pageable.class));
    }

    @Test
    @DisplayName("GET /v1/pollutions/history - Should handle custom pagination and sorting")
    void getPollutionHistoryByCityName_WithCustomPagination_ShouldApplyPagination() throws Exception
    {
        String cityName = "Ankara";
        List<PollutionQueryResponse> pollutions = List.of(pollutionResponse);
        when(pollutionService.getPollutionsByCityNameInRange(
                eq(cityName), any(DateFilterObject.class), any(Pageable.class)))
                .thenReturn(pollutions);

        mockMvc.perform(get("/v1/pollutions/history")
                        .param("city", cityName)
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "date,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());

        verify(pollutionService).getPollutionsByCityNameInRange(
                eq(cityName), any(DateFilterObject.class), argThat(pageable ->
                        pageable.getPageNumber() == 1 &&
                        pageable.getPageSize() == 10 &&
                        pageable.getSort().isSorted()
                ));
    }
}
