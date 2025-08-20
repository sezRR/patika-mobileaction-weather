package dev.sezrr.projects.patikaweatherproject.infrastructure.webclient.openweather;

import dev.sezrr.projects.patikaweatherproject.infrastructure.webclient.openweather.model.OWAPollutionHistory;
import dev.sezrr.projects.patikaweatherproject.model.city.query.CityQueryResponse;
import dev.sezrr.projects.patikaweatherproject.model.pollution.query.GetCityPollutionQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
public class OpenWeatherApiService
{
    private final WebClient geocodingApiClient;
    private final WebClient airPollutionApiClient;

    @Autowired
    public OpenWeatherApiService(@Qualifier("openWeatherGeocodingApiClient") WebClient geocodingApiClient,
                                 @Qualifier("openWeatherAirPollutionApiClient") WebClient airPollutionApiClient)
    {
        this.geocodingApiClient = geocodingApiClient;
        this.airPollutionApiClient = airPollutionApiClient;
    }

    public Optional<ResponseEntity<List<CityQueryResponse>>> getCoordinatesByCityName(String cityName)
    {
        return geocodingApiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("q", cityName)
                        .build()
                )
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<CityQueryResponse>>() {})
                .blockOptional();
    }

    public Optional<OWAPollutionHistory.QueryResponse> getPollutionHistoryByCityName(GetCityPollutionQueryRequest getCityPollutionQueryRequest)
    {
        // TODO: We can add retry mechanism here
        var startMillis = getCityPollutionQueryRequest.dateFilterObject().getStart().atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        var endMillis = getCityPollutionQueryRequest.dateFilterObject().getEnd().atTime(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC);
        return airPollutionApiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("lat", getCityPollutionQueryRequest.geospatialCoordinates().getLat())
                        .queryParam("lon", getCityPollutionQueryRequest.geospatialCoordinates().getLon())
                        .queryParam("start", startMillis)
                        .queryParam("end", endMillis)
                        .build()
                )
                .retrieve()
                .bodyToMono(OWAPollutionHistory.QueryResponse.class)
                .blockOptional();
    }
}
