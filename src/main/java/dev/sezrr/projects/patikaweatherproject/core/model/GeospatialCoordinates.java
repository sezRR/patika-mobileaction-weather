package dev.sezrr.projects.patikaweatherproject.core.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class GeospatialCoordinates
{
    @NotNull(message = "City latitude cannot be empty.")
    Double lat;

    @NotNull(message = "City longitude cannot be empty.")
    Double lon;
}
