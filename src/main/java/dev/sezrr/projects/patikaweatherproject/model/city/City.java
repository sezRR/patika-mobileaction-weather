package dev.sezrr.projects.patikaweatherproject.model.city;

import dev.sezrr.projects.patikaweatherproject.core.model.GeospatialCoordinates;
import dev.sezrr.projects.patikaweatherproject.core.model.uuid7.UuidV7;
import dev.sezrr.projects.patikaweatherproject.model.city.command.CreateNewCityCommand;
import dev.sezrr.projects.patikaweatherproject.model.city.query.CityQueryResponse;
import dev.sezrr.projects.patikaweatherproject.model.pollution.Pollution;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cities")
public class City {
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

    @OneToMany(mappedBy = "city")
    private List<Pollution> pollutions;

    public static class Mapper {
        public static City fromCommand(CreateNewCityCommand createNewCityCommand)
        {
            return City.builder()
                    .name(createNewCityCommand.name())
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
    }
}
