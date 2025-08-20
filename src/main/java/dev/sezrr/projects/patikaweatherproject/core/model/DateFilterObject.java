package dev.sezrr.projects.patikaweatherproject.core.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@Getter
@Setter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class DateFilterObject {
    @PastOrPresent
    @NotNull(message = "{validation.datefilterobject.start.notnull}")
    private LocalDate start;

    @PastOrPresent
    @NotNull(message = "{validation.datefilterobject.end.notnull}")
    private LocalDate end;

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

    @AssertTrue(message = "{validation.datefilterobject.hasvalidrange}")
    public boolean hasValidRange()
    {
        return start != null && end != null && !start.isAfter(end);
    }

    @AssertTrue(message = "{validation.datefilterobject.afterbusinessruledate}")
    public boolean isAfterBusinessRuleDate()
    {
        var businessRuleDate = LocalDate.of(2020, 11, 27);
        return start.isAfter(businessRuleDate) && end.isAfter(businessRuleDate);
    }

    @AssertTrue(message = "{validation.datefilterobject.iswithinoneyearrange}")
    public boolean isWithinOneYearRange()
    {
        return !start.isBefore(end.minusYears(1));
    }

    public static DateFilterObject normalizeMissingDateFilterObject(DateFilterObject dateFilterObject) {
        if (dateFilterObject != null)
            return dateFilterObject;

        var yesterday = LocalDate.now().minusDays(1).atStartOfDay();
        var newDateFilterObject = DateFilterObject.builder()
                .start(LocalDate.from(yesterday.minusWeeks(1)))
                .end(LocalDate.from(yesterday))
                .build();

        log.warn("Missing date range. Defaulting to {}..{}.", newDateFilterObject.getStart(), newDateFilterObject.getEnd());

        return newDateFilterObject;
    }
}
