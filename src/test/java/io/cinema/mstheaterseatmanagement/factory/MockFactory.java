package io.cinema.mstheaterseatmanagement.factory;

import io.cinema.mstheaterseatmanagement.domain.dto.request.OperatingHoursRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.OperatingHoursInfoResponseDto;
import lombok.experimental.UtilityClass;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class MockFactory {
    public static OperatingHoursInfoResponseDto buildOperatingHoursInfoResponseDto() {
        return new OperatingHoursInfoResponseDto(
                UUID.randomUUID(),
                DayOfWeek.FRIDAY,
                LocalTime.NOON,
                LocalTime.MIDNIGHT
        );
    }

    public static List<OperatingHoursRequestDto> buildOperatingHoursRequestDto() {
        var operatingHoursRequestDto = new OperatingHoursRequestDto(
                DayOfWeek.FRIDAY,
                LocalTime.NOON,
                LocalTime.MIDNIGHT
        );
        return List.of(operatingHoursRequestDto);
    }


}
