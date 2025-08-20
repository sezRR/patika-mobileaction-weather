package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi;

import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AQICategory;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.BaseAQIComponentCategorizerStrategy;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AirQualityComponent;

import java.util.List;
import java.util.Map;

public interface AQIAveragingService
{
    AQICategory calculateAveragingPeriodAndCategorizeComponent(BaseAQIComponentCategorizerStrategy baseAqiComponentCategorizerStrategy, List<Double> values);
    Map<AirQualityComponent, String> calculateAveragingPeriodsAndCategorizeComponents(Map<AirQualityComponent, List<Double>> aqiComponentsWithValues);
}
