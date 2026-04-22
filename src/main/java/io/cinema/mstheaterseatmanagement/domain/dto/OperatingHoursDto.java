package io.cinema.mstheaterseatmanagement.domain.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record OperatingHoursDto(
        DayOfWeek dayOfWeek,
        LocalTime start,
        LocalTime end
) {
}
