package dev.sezrr.projects.patikaweatherproject.model.pollution;

import dev.sezrr.projects.patikaweatherproject.core.model.AuditEntity;
import dev.sezrr.projects.patikaweatherproject.core.model.converter.MapToJsonConverter;
import dev.sezrr.projects.patikaweatherproject.core.model.uuid7.UuidV7;
import dev.sezrr.projects.patikaweatherproject.model.city.City;
import dev.sezrr.projects.patikaweatherproject.model.pollution.command.CreateNewPollutionCommand;
import dev.sezrr.projects.patikaweatherproject.model.pollution.query.PollutionQueryResponse;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pollutions")
public class Pollution extends AuditEntity {
    @Id
    @UuidV7
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Convert(converter = MapToJsonConverter.class)
    private Map<String, String> airQualityComponents;

    @Column(nullable = false)
    private LocalDate date;

    public static class Mapper {
        public static Pollution fromCommand(CreateNewPollutionCommand createNewPollutionCommand, City city) {
            var pollution = Pollution.builder()
                    .city(city)
                    .airQualityComponents(createNewPollutionCommand.airQualityComponents())
                    .date(createNewPollutionCommand.date())
                    .build();
            pollution.setActive(true);
            return pollution;
        }

        public static PollutionQueryResponse toQueryResponse(Pollution pollution) {
            return PollutionQueryResponse.builder()
                    .id(pollution.getId())
                    .cityName(pollution.getCity().getName())
                    .airQualityComponents(pollution.getAirQualityComponents())
                    .date(pollution.getDate())
                    .isActive(pollution.isActive())
                    .build();
        }

        public static Pollution fromQueryResponse(PollutionQueryResponse pollutionQueryResponse, City city) {
            return Pollution.builder()
                    .id(pollutionQueryResponse.id())
                    .city(city)
                    .airQualityComponents(pollutionQueryResponse.airQualityComponents())
                    .date(pollutionQueryResponse.date())
                    .build();
        }
    }
}
