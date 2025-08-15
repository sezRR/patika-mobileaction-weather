package dev.sezrr.projects.patikaweatherproject.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class DateFilterObject {
    @NotBlank(message = "Start time/date cannot be empty.")
    @NotNull(message = "Start time/date cannot be empty.")
    private LocalDateTime start;

    @NotBlank(message = "End time/date cannot be empty.")
    @NotNull(message = "End time/date cannot be empty.")
    private LocalDateTime end;
}
