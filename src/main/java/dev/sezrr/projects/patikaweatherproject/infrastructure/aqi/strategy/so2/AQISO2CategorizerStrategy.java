package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.strategy.so2;

import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AQICategory;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AQIComponent;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AirQualityComponent;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AveragingPeriod;

public class AQISO2CategorizerStrategy extends AQIComponent {
    public AQISO2CategorizerStrategy() {
        super(AirQualityComponent.SO2, AveragingPeriod.HOURS_24);
    }

    @Override
    public AQICategory categorize(Double averageValue) {
        if (!super.isAverageValueValid(averageValue)) {
            throw new IllegalArgumentException("Average value must be a valid number.");
        }

        if (averageValue <= 40.9) {
            return AQICategory.GOOD;
        } else if (averageValue <= 80.9) {
            return AQICategory.SATISFACTORY;
        } else if (averageValue <= 380.9) {
            return AQICategory.MODERATE;
        } else if (averageValue <= 800.9) {
            return AQICategory.POOR;
        } else if (averageValue <= 1600.0) {
            return AQICategory.SEVERE;
        } else {
            return AQICategory.HAZARDOUS;
        }
    }
}
