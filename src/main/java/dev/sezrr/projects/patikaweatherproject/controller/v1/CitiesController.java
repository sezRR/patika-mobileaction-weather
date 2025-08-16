package dev.sezrr.projects.patikaweatherproject.controller.v1;

import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.ResponseEntity;
import dev.sezrr.projects.patikaweatherproject.model.city.command.CreateNewCityCommand;
import dev.sezrr.projects.patikaweatherproject.model.city.query.CityQueryResponse;
import dev.sezrr.projects.patikaweatherproject.service.city.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/// /api/v1/cities/{city_id}/pollutions - GET, in the same controller or separate?
/// /api/v1/cities/{city_id}/pollutions - POST

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/cities")
@Tag(name = "Cities Controller", description = "City Command and Query endpoints")
public class CitiesController {
    private final CityService cityService;

    @GetMapping("")
    @Operation(
            summary = "Get all cities",
            description = "Retrieves a paginated list of all cities with their details."
    )
    public ResponseEntity<List<CityQueryResponse>> getAllCities(
            @PageableDefault(size = 5) @SortDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) 
    {
        return ResponseEntity.success(cityService.getAllCities(pageable));
    }
    
    @GetMapping("/{name}")
    @Operation(
            summary = "Get city by name",
            description = "Retrieves details of a city by its name. If the city does not exist in the database, it will be created using OpenWeather API."
    )
    public ResponseEntity<CityQueryResponse> getCityByName(@PathVariable String name)
    {
        return ResponseEntity.success(cityService.getCityByName(name));
    }
    
    @PostMapping("")
    @Operation(
            summary = "Create a new city",
            description = "Registers a new city and returns its details."
    )
    public ResponseEntity<CityQueryResponse> createNewCity(@RequestBody @Valid CreateNewCityCommand createNewCityCommand)
    {
        return ResponseEntity.success(cityService.createNewCity(createNewCityCommand));
    }
}
