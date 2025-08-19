package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.strategy;

import dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model.AQICategory;

import java.util.Optional;

public interface AQIComponentCategorizerStrategy
{
    AQICategory categorize(Double averageValue);
}
