package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.calculator;

import org.springframework.stereotype.Component;

import java.util.List;

@Component("HOURS_24")
public class Averaging24HoursPeriodCalculator
        extends AveragingPeriodArithmeticCalculator
        implements AQIAveragingPeriodCalculator {
    @Override
    public Double calculateAveragingPeriod(List<Double> values)
    {
        if (values == null || values.size() < 24)
        {
            throw new IllegalArgumentException("Values must be a non-null list with a size that is a greater than 24.");
        }

        Double average = 0.0;

        for (int i = 0; i < values.size(); i += 24)
        {
            average += super.calculateAveragingPeriod(values.subList(i, i + 24));
        }

        return average / (values.size() / 24);
    }
}
