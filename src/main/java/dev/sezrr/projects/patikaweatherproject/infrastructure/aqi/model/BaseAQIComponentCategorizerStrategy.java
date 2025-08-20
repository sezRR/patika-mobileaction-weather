package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model;

import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.strategy.AQIComponentCategorizerStrategy;
import lombok.Getter;

@Getter
public abstract class BaseAQIComponentCategorizerStrategy implements AQIComponentCategorizerStrategy
{
    protected final AirQualityComponent airQualityComponent;
    protected final AveragingPeriod averagingPeriod;

    protected BaseAQIComponentCategorizerStrategy(AirQualityComponent airQualityComponent, AveragingPeriod averagingPeriod)
    {
        this.airQualityComponent = airQualityComponent;
        this.averagingPeriod = averagingPeriod;
    }

    protected BaseAQIComponentCategorizerStrategy(String airQualityComponent, AveragingPeriod averagingPeriod)
    {
        this(AirQualityComponent.valueOf(airQualityComponent.toUpperCase()), averagingPeriod);
    }

    public boolean isAverageValueValid(Double averageValue)
    {
        return averageValue != null && !averageValue.isNaN() && !averageValue.isInfinite() && averageValue >= 0;
    }

    public AQICategory categorize(Double averageValue)
    {
        if (!isAverageValueValid(averageValue))
        {
            throw new IllegalArgumentException("Average value must be a valid number.");
        }

        if (averageValue < 0)
        {
            return AQICategory.NO_DATA;
        }

        return categorizeInternal(averageValue);
    }

    protected abstract AQICategory categorizeInternal(Double averageValue);

    public static String getClassifierStrategyName(AirQualityComponent airQualityComponent)
    {
        return airQualityComponent.name() + "_CATEGORIZER";
    }
}
