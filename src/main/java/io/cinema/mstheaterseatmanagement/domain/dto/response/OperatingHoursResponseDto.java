package io.cinema.mstheaterseatmanagement.domain.dto.response;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record OperatingHoursResponseDto(
        DayOfWeek dayOfWeek,
        LocalTime start,
        LocalTime end
) {
}
