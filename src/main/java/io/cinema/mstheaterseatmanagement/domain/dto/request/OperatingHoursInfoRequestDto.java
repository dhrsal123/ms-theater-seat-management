package io.cinema.mstheaterseatmanagement.domain.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record OperatingHoursInfoRequestDto(

        @NotNull(message = "The operating hours id must be valid.")
        UUID operatingHoursId,

        @NotNull(message = "Day of week is required")
        DayOfWeek dayOfWeek,

        @NotNull(message = "Start time is required")
        LocalTime start,

        @NotNull(message = "End time is required")
        LocalTime end
) {
}
