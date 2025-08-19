package dev.sezrr.projects.patikaweatherproject.infrastructure.aqi.model;

import lombok.Getter;

@Getter
public enum AQICategory
{
    NO_DATA("No Data"),
    GOOD("Good"),
    SATISFACTORY("Satisfactory"),
    MODERATE("Moderate"),
    POOR("Poor"),
    SEVERE("Severe"),
    HAZARDOUS("Hazardous");

    private final String category;

    AQICategory(String category) {
        this.category = category;
    }
}
