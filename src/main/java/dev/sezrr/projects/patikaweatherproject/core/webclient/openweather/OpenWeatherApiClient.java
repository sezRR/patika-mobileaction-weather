package dev.sezrr.projects.patikaweatherproject.core.webclient.openweather;

import dev.sezrr.projects.patikaweatherproject.core.config.OpenWeatherProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class OpenWeatherApiClient {
    private final OpenWeatherProperties openWeatherProperties;
    
    private Map<String, String> constructApiKeyUriVariable() {
        return Map.of("appid", openWeatherProperties.getApiKey());
    }
    
    public WebClient.Builder openWeatherApiClientBuilder() {
        return WebClient.builder()
                .baseUrl(openWeatherProperties.getBaseUrl());
    }
    
    @Bean
    public WebClient openWeatherGeocodingApiClient() {
        return WebClient.builder()
                .baseUrl(openWeatherProperties.getBaseUrl() + openWeatherProperties.getGeoPath())
                .defaultUriVariables(constructApiKeyUriVariable())
                .build();
    }

    @Bean
    public WebClient openWeatherAirPollutionApiClient() {
        return WebClient.builder()
                .baseUrl(openWeatherProperties.getBaseUrl() + openWeatherProperties.getAirPollutionPath())
                .defaultUriVariables(constructApiKeyUriVariable())
                .build();
    }
}
