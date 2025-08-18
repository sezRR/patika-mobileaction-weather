package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi;

import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AQICategory;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AQIComponent;

import java.util.List;

public interface AQIAveragingService {
    AQICategory calculateAveragingPeriodAndCategorizeComponent(AQIComponent aqiComponent, List<Double> values);
}
