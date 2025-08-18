package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.strategy.co;

import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AQICategory;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AQIComponent;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AirQualityComponent;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AveragingPeriod;

public class AQICOCategorizerStrategy extends AQIComponent {
    public AQICOCategorizerStrategy() {
        super(AirQualityComponent.CO, AveragingPeriod.HOURS_8);
    }

    @Override
    public AQICategory categorize(Double averageValue) {
        if (!super.isAverageValueValid(averageValue)) {
            throw new IllegalArgumentException("Average value must be a valid number.");
        }

        if (averageValue <= 100.0) {
            return AQICategory.GOOD;
        } else if (averageValue <= 200.0) {
            return AQICategory.SATISFACTORY;
        } else if (averageValue <= 1000.0) {
            return AQICategory.MODERATE;
        } else if (averageValue <= 1700.0) {
            return AQICategory.POOR;
        } else if (averageValue <= 3400.0) {
            return AQICategory.SEVERE;
        } else {
            return AQICategory.HAZARDOUS;
        }
    }
}
