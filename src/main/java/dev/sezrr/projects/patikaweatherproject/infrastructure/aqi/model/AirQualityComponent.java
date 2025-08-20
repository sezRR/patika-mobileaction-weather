package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model;

import lombok.Getter;

@Getter
public enum AirQualityComponent
{
    CO("CARBON MONOXIDE"),
    O3("OZONE"),
    SO2("SULFUR DIOXIDE"),;

    private final String componentFullName;

    AirQualityComponent(String componentFullName)
    {
        this.componentFullName = componentFullName;
    }
}
