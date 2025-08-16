package dev.sezrr.projects.patikaweatherproject.core.webclient.openweather;

import dev.sezrr.projects.patikaweatherproject.core.config.OpenWeatherProperties;
import dev.sezrr.projects.patikaweatherproject.model.city.query.CityQueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
public class OpenWeatherApiService {
    private final WebClient geocodingApiClient;
    private final WebClient airPollutionApiClient;
    private final OpenWeatherProperties openWeatherProperties;

    @Autowired
    public OpenWeatherApiService(@Qualifier("openWeatherGeocodingApiClient") WebClient geocodingApiClient,
                                 @Qualifier("openWeatherAirPollutionApiClient") WebClient airPollutionApiClient, OpenWeatherProperties openWeatherProperties) {
        this.geocodingApiClient = geocodingApiClient;
        this.airPollutionApiClient = airPollutionApiClient;
        this.openWeatherProperties = openWeatherProperties;
    }

    // TODO: Handler for WebClientResponseException
    public Optional<ResponseEntity<List<CityQueryResponse>>> getCoordinatesByCityName(String cityName) {
        return geocodingApiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("appid", openWeatherProperties.getApiKey())
                        .queryParam("q", cityName)
                        .build()
                )
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<CityQueryResponse>>() {})
                .blockOptional();
    }
}
