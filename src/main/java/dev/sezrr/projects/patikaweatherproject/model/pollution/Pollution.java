package dev.sezrr.projects.patikaweatherproject.model.pollution;

import dev.sezrr.projects.patikaweatherproject.core.model.AirQualityComponent;
import dev.sezrr.projects.patikaweatherproject.core.model.AuditEntity;
import dev.sezrr.projects.patikaweatherproject.core.model.DateFilterObject;
import dev.sezrr.projects.patikaweatherproject.core.model.converter.MapToJsonConverter;
import dev.sezrr.projects.patikaweatherproject.core.model.uuid7.UuidV7;
import dev.sezrr.projects.patikaweatherproject.model.city.City;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pollutions")
public class Pollution extends AuditEntity {
    @Id
    @UuidV7
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(columnDefinition = "jsonb")
    @Convert(converter = MapToJsonConverter.class)
    private Map<AirQualityComponent, Double> airQualityComponents;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "start", column = @Column(name = "filter_start")),
            @AttributeOverride(name = "end",   column = @Column(name = "filter_end"))
    })
    private DateFilterObject dateFilterObject;
}
