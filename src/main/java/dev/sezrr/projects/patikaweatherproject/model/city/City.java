package dev.sezrr.projects.patikaweatherproject.model.city;

import dev.sezrr.projects.patikaweatherproject.core.model.GeospatialCoordinates;
import dev.sezrr.projects.patikaweatherproject.core.model.uuid7.UuidV7;
import dev.sezrr.projects.patikaweatherproject.model.city.command.CreateNewCityCommand;
import dev.sezrr.projects.patikaweatherproject.model.city.query.CityQueryResponse;
import dev.sezrr.projects.patikaweatherproject.model.city.query.GetCityGeoCoordinatesQueryResponse;
import dev.sezrr.projects.patikaweatherproject.model.pollution.Pollution;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.text.WordUtils;

import java.util.List;
import java.util.UUID;

@Slf4j
@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cities")
public class City
{
    @Id
    @UuidV7
    private UUID id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "City name cannot be empty.")
    @NotNull(message = "City name cannot be empty.")
    private String name;

    private GeospatialCoordinates geospatialCoordinates;

    private String country;
    private String state;

    @OneToMany(mappedBy = "city", cascade =  CascadeType.ALL, orphanRemoval = true)
    private List<Pollution> pollutions;

    public static class Mapper
    {
        public static City fromCommand(CreateNewCityCommand createNewCityCommand)
        {
            return City.builder()
                    .name(normalizedName(createNewCityCommand.name().getCityName()))
                    .geospatialCoordinates(createNewCityCommand.geospatialCoordinates())
                    .country(createNewCityCommand.country())
                    .state(createNewCityCommand.state())
                    .build();
        }

        public static CityQueryResponse toQueryResponse(City city)
        {
            return CityQueryResponse.builder()
                    .id(city.getId())
                    .name(city.getName())
                    .geospatialCoordinates(city.getGeospatialCoordinates())
                    .country(city.getCountry())
                    .state(city.getState())
                    .build();
        }

        public static City fromQueryResponse(GetCityGeoCoordinatesQueryResponse getCityGeoCoordinatesQueryResponse)
        {
            return City.builder()
                    .id(getCityGeoCoordinatesQueryResponse.id())
                    .name(getCityGeoCoordinatesQueryResponse.name())
                    .geospatialCoordinates(getCityGeoCoordinatesQueryResponse.geospatialCoordinates())
                    .build();
        }
    }

    public static String normalizedName(String name)
    {
        if (name == null || name.isBlank())
        {
            log.warn("City name is null or blank, returning null.");
            return null;
        }

        var locale = LocaleUtils.toLocale("en_US");

        return WordUtils.capitalizeFully(name.toLowerCase(locale), ' ');
    }
}
