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

--changeset cinema-system:2
-- Description: Add auditing columns to the address table
ALTER TABLE address
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN created_by VARCHAR(255),
    ADD COLUMN updated_by VARCHAR(255);

--changeset cinema-system:3
-- Description: Rename theater and operating_hours tables to singular
ALTER TABLE theaters
    RENAME TO theater;
ALTER TABLE operating_hours
    RENAME TO operating_hour;

--changeset cinema-system:4
-- Description: Add room and seat tables with CHECK constraints for enums
CREATE TABLE IF NOT EXISTS room
(
    id         UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    name       VARCHAR(255) NOT NULL,
    theater_id UUID         NOT NULL REFERENCES theater (id) ON DELETE CASCADE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS seat
(
    id              UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    price_increment DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    row_number      INTEGER          NOT NULL,
    col_number      INTEGER          NOT NULL,
    seat_status     VARCHAR(50)      NOT NULL,
    seat_type       VARCHAR(50)      NOT NULL,
    room_id         UUID             NOT NULL REFERENCES room (id) ON DELETE CASCADE,
    created_at      TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP,
    created_by      VARCHAR(255),
    updated_by      VARCHAR(255),
    CONSTRAINT uq_room_seat_coordinates UNIQUE (room_id, row_number, col_number),
    CONSTRAINT chk_seat_status CHECK (seat_status IN ('OPERATIONAL', 'UNDER_MAINTENANCE')),
    CONSTRAINT chk_seat_type CHECK (seat_type IN ('STANDARD', 'PREMIUM', 'COUPLE_SEAT', 'WHEELCHAIR_ACCESSIBLE', 'COMPANION', 'EASY_ACCESS'))
);