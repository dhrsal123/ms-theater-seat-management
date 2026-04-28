package io.cinema.mstheaterseatmanagement.domain.dto.response;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record OperatingHoursInfoResponseDto(
        UUID operatingHoursId,
        DayOfWeek dayOfWeek,
        LocalTime start,
        LocalTime end
) {
}
