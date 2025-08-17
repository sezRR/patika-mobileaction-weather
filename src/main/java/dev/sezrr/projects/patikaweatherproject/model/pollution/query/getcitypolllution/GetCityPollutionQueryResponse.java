package dev.sezrr.projects.patikaweatherproject.model.pollution.query.getcitypolllution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

// TODO: FIX
@Builder
public record GetCityPollutionQueryResponse(
        double longitude,
        double latitude,
        List<PollutionMeasurement> measurements
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PollutionMeasurement(
            int aqi,
            double co,
            double o3,
            double so2,
            LocalDateTime timestamp
    ) {}
}

