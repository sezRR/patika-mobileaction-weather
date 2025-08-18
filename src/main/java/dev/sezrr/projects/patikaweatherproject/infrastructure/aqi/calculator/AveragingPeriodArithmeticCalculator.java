package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.calculator;

import java.util.List;

public abstract class AveragingPeriodArithmeticCalculator implements AQIAveragingPeriodCalculator
{
    @Override
    public Double calculateAveragingPeriod(List<Double> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("AveragingPeriodCalculatorDecorator requires at least one value");
        }

        double sum = 0.0;
        for (Double value : values)
        {
            sum += value;
        }

        return sum / values.size();
    }
}
