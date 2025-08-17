package dev.sezrr.projects.patikaweatherproject.core.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class DateFilterObject {
    @PastOrPresent
    @NotNull(message = "Start date cannot be empty.")
    private LocalDate start;

    @FutureOrPresent
    @NotNull(message = "End date cannot be empty.")
    private LocalDate end;

    @AssertTrue(message = "Start date must be before or equal to end date.")
    public boolean isValidRange()
    {
        return start != null && end != null && !start.isAfter(end);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DateFilterObject that = (DateFilterObject) o;
        return Objects.equals(start, that.start) && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
