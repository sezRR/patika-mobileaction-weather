package dev.sezrr.projects.patikaweatherproject.core.util;

import dev.sezrr.projects.patikaweatherproject.core.model.DateFilterObject;

import java.time.LocalDate;
import java.util.*;

public class DateHelper {
    public static List<LocalDate> findMissingDatesInRange(DateFilterObject dateFilterObject, List<LocalDate> existingDates) {
        if (!dateFilterObject.isValidRange())
        {
            throw new IllegalArgumentException("Invalid date range provided.");
        }

        Set<LocalDate> existing = existingDates == null ?
                Collections.emptySet()
                : new HashSet<>(existingDates);

        List<LocalDate> missing = new ArrayList<>();
        for (LocalDate d = dateFilterObject.getStart(); !d.isAfter(dateFilterObject.getEnd()); d = d.plusDays(1)) {
            if (!existing.contains(d)) {
                missing.add(d);
            }
        }

        return missing;
    }

    public static List<DateFilterObject> generateIntervals(List<LocalDate> dates)
    {
        if (dates == null || dates.isEmpty()) {
            return Collections.emptyList();
        }

        if (dates.size() == 1) {
            LocalDate singleDate = dates.get(0);
            return List.of(new DateFilterObject(singleDate, singleDate));
        }

        // Normalize: unique + sort
        List<LocalDate> sorted = dates.stream()
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        List<DateFilterObject> intervals = new ArrayList<>();
        var start = 0;
        var left = 0;
        var right = 1;

        while (right < sorted.size())
        {
            LocalDate leftDate = sorted.get(left);
            LocalDate rightDate = sorted.get(right);

            // If dates are consecutive, extend the current interval
            if (leftDate.plusDays(1).equals(rightDate)) {
                right++;
                left++;
            } else {
                // Otherwise, close the current interval and start a new one
                intervals.add(new DateFilterObject(sorted.get(start), sorted.get(left)));
                start = right;
                left = right;
                right++;
            }
        }

        intervals.add(new DateFilterObject(sorted.get(start), sorted.get(left)));

        return intervals;
    }
}
