package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.calculator;

import org.springframework.stereotype.Component;

import java.util.List;

@Component("HOURS_8")
public class Averaging8HoursPeriodCalculator
        extends AveragingPeriodArithmeticCalculator
        implements AQIAveragingPeriodCalculator
{
    @Override
    public Double calculateAveragingPeriod(List<Double> values)
    {
        if (values == null || values.size() < 8)
        {
            throw new IllegalArgumentException("Values must be a non-null list with a size that is a greater than 8.");
        }

        Double average = 0.0;

        for (int i = 0; i < values.size(); i += 8)
        {
            average += super.calculateAveragingPeriod(values.subList(i, i + 8));
        }

        return average / (values.size() / 8);
    }
}
