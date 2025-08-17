package dev.sezrr.projects.patikaweatherproject.core.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "openweather")
public class OpenWeatherProperties {
    @Value("${openweather.base-url}")
    private String baseUrl;

    @Value("${openweather.geo-path}")
    private String geoPath;

    @Value("${openweather.air-pollution-history-path}")
    private String airPollutionPath;

    @Value("${openweather.api-key}")
    private String apiKey;
}
