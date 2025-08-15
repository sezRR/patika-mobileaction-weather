package dev.sezrr.projects.patikaweatherproject.controller.v1;

import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.ResponseEntity;
import dev.sezrr.projects.patikaweatherproject.model.city.command.CreateNewCityCommand;
import dev.sezrr.projects.patikaweatherproject.model.city.query.CityQueryResponse;
import dev.sezrr.projects.patikaweatherproject.service.CityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// /api/v1/cities/{city_id}/pollutions - GET, in the same controller or separate?
/// /api/v1/cities/{city_id}/pollutions - POST
/// /api/v1/cities/{city_id} - GET
/// /api/v1/cities/{city_id} - POST

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/cities")
@Tag(name = "Cities Controller", description = "City Command and Query endpoints")
public class CitiesController {
    private final CityService cityService;

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
