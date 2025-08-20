package dev.sezrr.projects.patikaweatherproject.controller.v1;

import dev.sezrr.projects.patikaweatherproject.core.model.DateFilterObject;
import dev.sezrr.projects.patikaweatherproject.core.model.customresponseentity.ResponseEntity;
import dev.sezrr.projects.patikaweatherproject.model.pollution.command.CreateNewPollutionCommand;
import dev.sezrr.projects.patikaweatherproject.model.pollution.query.PollutionQueryResponse;
import dev.sezrr.projects.patikaweatherproject.service.pollution.PollutionService;
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
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/pollutions")
@Tag(name = "Pollutions Controller", description = "Pollution Command and Query endpoints")
public class PollutionsController
{
    private final PollutionService pollutionService;

    @GetMapping("/history/all")
    @Operation(
            summary = "Get all pollutions",
            description = "Retrieves a paginated list of all pollution records with their details."
    )
    public ResponseEntity<List<PollutionQueryResponse>> getAllPollutions(
            @PageableDefault(size = 5) @SortDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable
    )
    {
        return ResponseEntity.success(pollutionService.getAllPollutions(pageable));
    }

    @GetMapping("/history")
    @Operation(
            summary = "Get pollution history by city name",
            description = "Retrieves a paginated list of pollution records for a specific city."
    )
    public ResponseEntity<List<PollutionQueryResponse>> getPollutionHistoryByCityName(
            @RequestParam String city,
            @ModelAttribute DateFilterObject dateFilterObject,
            @PageableDefault(size = 5) @SortDefault(sort = "date", direction = Sort.Direction.DESC) Pageable pageable
    )
    {
        return ResponseEntity.success(pollutionService.getPollutionsByCityNameInRange(
                city,
                dateFilterObject,
                pageable)
        );
    }

    @PostMapping("/history")
    @Operation(
            summary = "Create a new pollution record",
            description = "Creates a new pollution record for a specific city with the provided data."
    )
    public ResponseEntity<PollutionQueryResponse> createNewPollution(
            @RequestBody @Valid CreateNewPollutionCommand createNewPollutionCommand
    )
    {
        return ResponseEntity.success(pollutionService.createNewPollution(createNewPollutionCommand));
    }

    @DeleteMapping("/history/{id}")
    @Operation(
            summary = "Delete a pollution record",
            description = "Deletes a pollution record by its Id."
    )
    public ResponseEntity<Void> deletePollutionById(
            @PathVariable UUID id
    )
    {
        pollutionService.deletePollutionById(id);
        return ResponseEntity.emptyNoContent();
    }

    @DeleteMapping("/history/city/{cityName}")
    @Operation(
            summary = "Delete all pollution records by city name",
            description = "Deletes all pollution records for a specific city."
    )
    public ResponseEntity<Void> deletePollutionByCityName(
            @PathVariable String cityName
    )
    {
        pollutionService.deletePollutionByCityName(cityName);
        return ResponseEntity.emptyNoContent();
    }
}
