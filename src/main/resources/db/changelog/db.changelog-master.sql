--liquibase formatted sql

--changeset cinema-system:1
-- Description: Initial schema creation with existence checks
CREATE TABLE IF NOT EXISTS address
(
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    street  VARCHAR(255) NOT NULL,
    city    VARCHAR(100) NOT NULL,
    state   VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    zip     VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS theaters
(
    id         UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    name       VARCHAR(255) NOT NULL,
    phone      VARCHAR(50)  NOT NULL,
    email      VARCHAR(255) NOT NULL,
    address_id UUID         NOT NULL UNIQUE REFERENCES address (id) ON DELETE CASCADE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS operating_hours
(
    id          UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    day_of_week VARCHAR(15) NOT NULL,
    start_time  TIME        NOT NULL,
    end_time    TIME        NOT NULL,
    theater_id  UUID        NOT NULL REFERENCES theaters (id) ON DELETE CASCADE,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP,
    created_by  VARCHAR(255),
    updated_by  VARCHAR(255),
    CONSTRAINT uq_theater_day_of_week UNIQUE (theater_id, day_of_week)
);