package io.cinema.mstheaterseatmanagement.factory;

import io.cinema.mstheaterseatmanagement.domain.dto.request.OperatingHoursRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.OperatingHoursInfoResponseDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.OperatingHoursResponseDto;
import io.cinema.mstheaterseatmanagement.domain.entity.OperatingHoursEntity;
import lombok.experimental.UtilityClass;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class OperatingHoursMockFactory {
    public static OperatingHoursInfoResponseDto buildOperatingHoursInfoResponseDto(UUID operatingHourId) {
        return new OperatingHoursInfoResponseDto(
                operatingHourId,
                DayOfWeek.FRIDAY,
                LocalTime.NOON,
                LocalTime.MIDNIGHT
        );
    }


    public static OperatingHoursResponseDto buildOperatingHoursResponseDto() {
        return new OperatingHoursResponseDto(
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

    public static OperatingHoursEntity buildOperatingHoursEntity(UUID operatingHourId, UUID theaterId) {
        return new OperatingHoursEntity(
                operatingHourId,
                DayOfWeek.FRIDAY,
                LocalTime.NOON,
                LocalTime.MIDNIGHT,
                theaterId
        );
    }


}
