package dev.sezrr.projects.patikaweatherproject.infrastructure.webclient.openweather.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class OWAPollutionHistory {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Components
    {
        private Double co;
        private Double o3;
        private Double so2;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data
    {
        private Components components;
        private long dt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueryResponse
    {
        private List<Data> list;
    }
}

