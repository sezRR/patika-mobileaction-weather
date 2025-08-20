package dev.sezrr.projects.patikaweatherproject.infrastructure.webclient.openweather;

import dev.sezrr.projects.patikaweatherproject.core.config.OpenWeatherProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class OpenWeatherApiClient
{
    private final OpenWeatherProperties openWeatherProperties;
    
    private Map<String, String> constructApiKeyUriVariable()
    {
        return Map.of("appid", openWeatherProperties.getApiKey());
    }

    // https://stackoverflow.com/questions/59735951/databufferlimitexception-exceeded-limit-on-max-bytes-to-buffer-webflux-error
    private final ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize((int) DataSize.ofMegabytes(10).toBytes()))
            .build();

    @Bean
    public WebClient openWeatherGeocodingApiClient()
    {
        return WebClient.builder()
                .baseUrl(openWeatherProperties.getBaseUrl() + openWeatherProperties.getGeoPath())
                .defaultUriVariables(constructApiKeyUriVariable())
                .build();
    }

    @Bean
    public WebClient openWeatherAirPollutionApiClient()
    {
        return WebClient.builder()
                .exchangeStrategies(strategies)
                .baseUrl(openWeatherProperties.getBaseUrl() + openWeatherProperties.getAirPollutionPath())
                .defaultUriVariables(constructApiKeyUriVariable())
                .build();
    }
}
