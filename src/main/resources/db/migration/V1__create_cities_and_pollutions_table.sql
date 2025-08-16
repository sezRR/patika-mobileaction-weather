CREATE TABLE cities
(
    id      UUID         NOT NULL,
    name    VARCHAR(255) NOT NULL,
    country VARCHAR(255),
    state   VARCHAR(255),
    lat     DOUBLE PRECISION,
    lon     DOUBLE PRECISION,
    CONSTRAINT "pk_cities" PRIMARY KEY (id)
);

CREATE TABLE pollutions
(
    id                     UUID NOT NULL,
    is_active              BOOLEAN,
    created_at             TIMESTAMP WITHOUT TIME ZONE,
    updated_at             TIMESTAMP WITHOUT TIME ZONE,
    deleted_at             TIMESTAMP WITHOUT TIME ZONE,
    city_id                UUID NOT NULL,
    air_quality_components JSONB,
    filter_start           TIMESTAMP WITHOUT TIME ZONE,
    filter_end             TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT "pk_pollutions" PRIMARY KEY (id)
);

ALTER TABLE cities
    ADD CONSTRAINT "uc_cities_name" UNIQUE (name);

ALTER TABLE pollutions
    ADD CONSTRAINT FK_POLLUTIONS_ON_CITY FOREIGN KEY (city_id) REFERENCES cities (id);