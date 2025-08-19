package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi;

import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.calculator.AQIAveragingPeriodCalculator;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AQICategory;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.BaseAQIComponentCategorizerStrategy;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AirQualityComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AQIAveragingServiceImpl implements AQIAveragingService {
    private final Map<String, AQIAveragingPeriodCalculator> averagingCalculators;
    private final Map<String, BaseAQIComponentCategorizerStrategy> aqiComponents;

    public AQICategory calculateAveragingPeriodAndCategorizeComponent(BaseAQIComponentCategorizerStrategy baseAqiComponentCategorizerStrategy, List<Double> values) {
        AQIAveragingPeriodCalculator calculator = averagingCalculators.get(baseAqiComponentCategorizerStrategy.getAveragingPeriod().name().toUpperCase());
        if (calculator == null) {
            throw new IllegalArgumentException("Unsupported averaging period for " + baseAqiComponentCategorizerStrategy.getAveragingPeriod());
        }

        var avg = calculator.calculateAveragingPeriod(values);
        return baseAqiComponentCategorizerStrategy.categorize(avg);
    }

    @Override
    public Map<AirQualityComponent, String> calculateAveragingPeriodsAndCategorizeComponents(Map<AirQualityComponent, List<Double>> aqiComponentsWithValues) {
        if (aqiComponentsWithValues == null || aqiComponentsWithValues.isEmpty()) {
            throw new IllegalArgumentException("No AQI components provided for averaging and categorization.");
        }

        Map<AirQualityComponent, String> categories = new HashMap<>();
        for (AirQualityComponent aqiComponent : aqiComponentsWithValues.keySet()) {
            var classifiedAQIComponent = calculateAveragingPeriodAndCategorizeComponent(
                    aqiComponents.get(BaseAQIComponentCategorizerStrategy.getClassifierStrategyName(aqiComponent)), // getClassifierStrategyName is a utility method to get the correct strategy name (ELEMENT_CLASSIFIER)
                    aqiComponentsWithValues.get(aqiComponent)
            );
            categories.put(aqiComponent, classifiedAQIComponent.name());
        }

        return categories;
    }
}
