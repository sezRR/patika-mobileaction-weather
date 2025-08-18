package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi;

import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.calculator.AQIAveragingPeriodCalculator;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AQICategory;
import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AQIComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AQIAveragingServiceImpl implements AQIAveragingService {
    private final Map<String, AQIAveragingPeriodCalculator> averagingCalculators;

    public AQICategory calculateAveragingPeriodAndCategorizeComponent(AQIComponent aqiComponent, List<Double> values) {
        AQIAveragingPeriodCalculator calculator = averagingCalculators.get(aqiComponent.getAveragingPeriod().name().toUpperCase());
        if (calculator == null) {
            throw new IllegalArgumentException("Unsupported averaging period for " + aqiComponent.getAveragingPeriod());
        }

        var avg = calculator.calculateAveragingPeriod(values);
        if (!aqiComponent.isAverageValueValid(avg)) {
            throw new IllegalArgumentException("Invalid average value: " + avg);
        }

        return aqiComponent.categorize(avg);
    }
}
