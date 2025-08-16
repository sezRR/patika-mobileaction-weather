package dev.sezrr.projects.patikaweatherproject.model.city.query;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import dev.sezrr.projects.patikaweatherproject.core.model.GeospatialCoordinates;
import jakarta.persistence.Embedded;
import lombok.Builder;

import java.util.UUID;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record CityQueryResponse(
        UUID id,
        String name,
        @JsonUnwrapped GeospatialCoordinates geospatialCoordinates,
        String country,
        String state
)
{

}
