-- liquibase formatted sql

-- changeset denismalinin:1731108128780-1
CREATE SEQUENCE IF NOT EXISTS events_seq START WITH 1 INCREMENT BY 50;

-- changeset denismalinin:1731108128780-2
CREATE SEQUENCE IF NOT EXISTS places_seq START WITH 1 INCREMENT BY 50;

-- changeset denismalinin:1731108128780-3
CREATE TABLE events
(
    id       BIGINT NOT NULL,
    name     VARCHAR(255) NOT NULL ,
    date     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    place_id BIGINT NOT NULL ,
    CONSTRAINT pk_events PRIMARY KEY (id)
);

-- changeset denismalinin:1731108128780-4
CREATE TABLE places
(
    id   BIGINT NOT NULL,
    slug VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL ,
    CONSTRAINT pk_places PRIMARY KEY (id)
);

-- changeset denismalinin:1731108128780-5
ALTER TABLE events
    ADD CONSTRAINT FK_EVENTS_ON_PLACE FOREIGN KEY (place_id) REFERENCES places (id);

