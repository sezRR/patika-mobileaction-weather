package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.strategy.co;

import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AQICategory;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.BaseAQIComponentCategorizerStrategy;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AirQualityComponent;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AveragingPeriod;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("CO_CATEGORIZER")
public class AQICOCategorizerStrategy extends BaseAQIComponentCategorizerStrategy {
    public AQICOCategorizerStrategy() {
        super(AirQualityComponent.CO, AveragingPeriod.HOURS_8);
    }

    @Override
    protected AQICategory categorizeInternal(Double averageValue) {
        if (averageValue <= 1000.0) {
            return AQICategory.GOOD;
        } else if (averageValue <= 2000.0) {
            return AQICategory.SATISFACTORY;
        } else if (averageValue <= 10000.0) {
            return AQICategory.MODERATE;
        } else if (averageValue <= 17000.0) {
            return AQICategory.POOR;
        } else if (averageValue <= 34000.0) {
            return AQICategory.SEVERE;
        } else {
            return AQICategory.HAZARDOUS;
        }
    }
}
