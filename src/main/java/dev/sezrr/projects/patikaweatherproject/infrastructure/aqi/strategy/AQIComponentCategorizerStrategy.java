package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.strategy;

import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AQICategory;

public interface AQIComponentCategorizerStrategy
{
    AQICategory categorize(Double averageValue);
}
