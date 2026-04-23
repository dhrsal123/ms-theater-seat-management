package io.cinema.mstheaterseatmanagement.domain.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record OperatingHoursRequestDto(
        @NotNull(message = "Day of week is required")
        DayOfWeek dayOfWeek,

        @NotNull(message = "Start time is required")
        LocalTime start,

        @NotNull(message = "End time is required")
        LocalTime end
) {
}
