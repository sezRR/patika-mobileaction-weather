package dev.sezrr.projects.patikaweatherproject.core.util;

import dev.sezrr.projects.patikaweatherproject.core.model.DateFilterObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DateHelperTests {
    @Test
    @DisplayName("findMissingDatesInRange: finds dates not included in input within range")
    void findMissingDatesInRange_basic() {
        var start = LocalDate.of(2025, 8, 10);
        var end   = LocalDate.of(2025, 8, 15);
        var range = new DateFilterObject(start, end);

        var existing = List.of(
                LocalDate.of(2025, 8, 10),
                LocalDate.of(2025, 8, 12),
                LocalDate.of(2025, 8, 14)
        );

        var missing = DateHelper.findMissingDatesInRange(range, existing);
        assertEquals(List.of(
                LocalDate.of(2025, 8, 11),
                LocalDate.of(2025, 8, 13),
                LocalDate.of(2025, 8, 15)
        ), missing);
    }

    @Test
    @DisplayName("generateIntervals: single date becomes a single [d..d] interval")
    void generateIntervals_singleDate() {
        var d = LocalDate.of(2025, 8, 17);
        var result = DateHelper.generateIntervals(List.of(d));
        var expected = List.of(new DateFilterObject(d, d));
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("generateIntervals: builds four runs from spaced dates (now-1w..now-1w), (now..now), (now+2w..now+2w) and (now+3w..now+3w)")
    void generateIntervals_fourRuns() {
        var minus1w = LocalDate.now().minusWeeks(1);
        var now = LocalDate.now();
        var plus2w = LocalDate.now().plusWeeks(2);
        var plus3w = LocalDate.now().plusWeeks(3);

        var dates = List.of(minus1w, now, plus2w, plus3w);
        var intervals = DateHelper.generateIntervals(dates);

        var expected = List.of(
            new DateFilterObject(minus1w, minus1w),
            new DateFilterObject(now, now),
            new DateFilterObject(plus2w, plus2w),
            new DateFilterObject(plus3w, plus3w)
        );
        assertEquals(expected, intervals);
    }

    @Test
    @DisplayName("generateIntervals: handles unsorted input and duplicates")
    void generateIntervals_unsortedDuplicates() {
        var a = LocalDate.of(2025, 8, 10);
        var b = LocalDate.of(2025, 8, 11);
        var c = LocalDate.of(2025, 8, 12);
        var e = LocalDate.of(2025, 8, 14);

        var dates = List.of(c, a, b, b, e, e); // unsorted + dups

        var intervals = DateHelper.generateIntervals(dates);

        assertEquals(List.of(
                new DateFilterObject(a, c),
                new DateFilterObject(e, e)
        ), intervals);
    }

    @Test
    @DisplayName("generateIntervals: empty input returns empty list")
    void generateIntervals_empty() {
        assertTrue(DateHelper.generateIntervals(List.of()).isEmpty());
    }
}
