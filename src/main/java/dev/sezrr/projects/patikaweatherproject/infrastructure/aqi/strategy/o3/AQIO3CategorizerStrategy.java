package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.strategy.o3;

import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AQICategory;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AQIComponent;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AirQualityComponent;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AveragingPeriod;

public class AQIO3CategorizerStrategy extends AQIComponent {
    public AQIO3CategorizerStrategy() {
        super(AirQualityComponent.O3, AveragingPeriod.HOURS_8);
    }

    @Override
    public AQICategory categorize(Double averageValue) {
        if (!super.isAverageValueValid(averageValue)) {
            throw new IllegalArgumentException("Average value must be a valid number.");
        }

        if (averageValue <= 50.9) {
            return AQICategory.GOOD;
        } else if (averageValue <= 100.9) {
            return AQICategory.SATISFACTORY;
        } else if (averageValue <= 168.9) {
            return AQICategory.MODERATE;
        } else if (averageValue <= 208.9) {
            return AQICategory.POOR;
        } else if (averageValue <= 748.0) {
            return AQICategory.SEVERE;
        } else {
            return AQICategory.HAZARDOUS;
        }
    }
}
