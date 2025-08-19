package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.strategy.so2;

import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AQICategory;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.BaseAQIComponentCategorizerStrategy;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AirQualityComponent;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AveragingPeriod;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("SO2_CATEGORIZER")
public class AQISO2CategorizerStrategy extends BaseAQIComponentCategorizerStrategy {
    public AQISO2CategorizerStrategy() {
        super(AirQualityComponent.SO2, AveragingPeriod.HOURS_24);
    }

    @Override
    protected AQICategory categorizeInternal(Double averageValue) {
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
