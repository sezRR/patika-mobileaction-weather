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
    @NotNull(message = "Start date cannot be empty.")
    private LocalDate start;

    @PastOrPresent
    @NotNull(message = "End date cannot be empty.")
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

    // TODO: Custom constraint annotations, or get rid of statics
    public static class Validator
    {
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

        @AssertTrue(message = "Invalid date range.")
        public static boolean isValid(DateFilterObject dateFilterObject)
        {
            return isValidRange(dateFilterObject)
                    && datesShouldNotBeforeFromGivenDate(dateFilterObject)
                    && dateRangesShouldNotMoreThanOneYear(dateFilterObject);
        }

        @AssertTrue(message = "Invalid date range.")
        public static boolean isValid(DateFilterObject dateFilterObject, LocalDate givenDate)
        {
            return isValidRange(dateFilterObject)
                    && datesShouldNotBeforeFromGivenDate(dateFilterObject, givenDate)
                    && dateRangesShouldNotMoreThanOneYear(dateFilterObject);
        }

        @AssertTrue(message = "Start date must be before or equal to end date.")
        public static boolean isValidRange(DateFilterObject dateFilterObject)
        {
            return dateFilterObject.getStart() != null && dateFilterObject.getEnd() != null && !dateFilterObject.getStart().isAfter(dateFilterObject.getEnd());
        }

        @AssertTrue(message = "Start and end date must not be before from the 2020/11/27.")
        public static boolean datesShouldNotBeforeFromGivenDate(DateFilterObject dateFilterObject)
        {
            var businessRuleDate = LocalDate.of(2020, 11, 27);
            return dateFilterObject.getStart().isAfter(businessRuleDate) && dateFilterObject.getEnd().isAfter(businessRuleDate);
        }

        @AssertTrue(message = "Start and end date must not be before from the given date.")
        public static boolean datesShouldNotBeforeFromGivenDate(DateFilterObject dateFilterObject, LocalDate givenDate)
        {
            if (givenDate == null) {
                throw new IllegalArgumentException("Given date cannot be null.");
            }

            return dateFilterObject.getStart().isAfter(givenDate) && dateFilterObject.getEnd().isAfter(givenDate);
        }

        @AssertTrue(message = "Date ranges should not be more than one year apart.")
        public static boolean dateRangesShouldNotMoreThanOneYear(DateFilterObject dateFilterObject)
        {
            return !dateFilterObject.getStart().isBefore(dateFilterObject.getEnd().minusYears(1));
        }
    }
}
