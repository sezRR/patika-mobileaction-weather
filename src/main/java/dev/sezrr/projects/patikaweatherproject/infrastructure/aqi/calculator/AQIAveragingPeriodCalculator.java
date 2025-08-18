package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.calculator;

import java.util.List;

public interface AQIAveragingPeriodCalculator
{
    Double calculateAveragingPeriod(List<Double> values);
}
