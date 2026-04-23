package io.cinema.mstheaterseatmanagement.domain.entity;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record TheaterRowProjection(
        UUID theaterId,
        String name,
        String email,
        String phone,
        String street,
        String city,
        String state,
        String country,
        String zip,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {
}
