package dev.sezrr.projects.patikaweatherproject.model.city;

import lombok.Getter;

@Getter
public enum AllowedCityName
{
    LONDON("London"),
    BARCELONA("Barcelona"),
    ANKARA("Ankara"),
    TOKYO("Tokyo"),
    MUMBAI("Mumbai");

    private final String cityName;

    AllowedCityName(String cityName)
    {
        this.cityName = City.normalizedName(cityName);
    }
}
