package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model;

import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.strategy.AQIComponentCategorizerStrategy;
import lombok.Getter;

@Getter
public abstract class AQIComponent implements AQIComponentCategorizerStrategy {
    protected final AirQualityComponent airQualityComponent;
    protected final AveragingPeriod averagingPeriod;

    protected AQIComponent(AirQualityComponent airQualityComponent, AveragingPeriod averagingPeriod) {
        this.airQualityComponent = airQualityComponent;
        this.averagingPeriod = averagingPeriod;
    }

    protected AQIComponent(String airQualityComponent, AveragingPeriod averagingPeriod) {
        this(AirQualityComponent.valueOf(airQualityComponent.toUpperCase()), averagingPeriod);
    }

    public boolean isAverageValueValid(Double averageValue) {
        return averageValue != null && !averageValue.isNaN() && !averageValue.isInfinite() && averageValue > 0;
    }
}
