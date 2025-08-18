package dev.sezrr.projects.patikaweatherproject.core.webclient.openweather.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class OWAPollutionHistoryComponents
{
    private Double co;
    private Double o3;
    private Double so2;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class OWAPollutionHistoryData
{
    private OWAPollutionHistoryComponents components;
    private long dt;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OWAPollutionHistoryQueryResponse
{
    private List<OWAPollutionHistoryData> list;
}

