package io.cinema.mstheaterseatmanagement.factory;

import io.cinema.domain.enumerated.CinemaExceptionTypes;
import io.cinema.domain.exceptions.CinemaException;
import io.cinema.mstheaterseatmanagement.domain.dto.request.OperatingHoursRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.OperatingHoursInfoResponseDto;
import io.cinema.mstheaterseatmanagement.domain.entity.OperatingHoursEntity;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterEntity;
import lombok.experimental.UtilityClass;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.transaction.CannotCreateTransactionException;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@UtilityClass
public class MockFactory {
    public static OperatingHoursInfoResponseDto buildOperatingHoursInfoResponseDto(UUID operatingHourId) {
        return new OperatingHoursInfoResponseDto(
                operatingHourId,
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

    public static OperatingHoursEntity buildOperatingHoursEntity (UUID operatingHourId, UUID theaterId) {
        return new OperatingHoursEntity(
                operatingHourId,
                DayOfWeek.FRIDAY,
                LocalTime.NOON,
                LocalTime.MIDNIGHT,
                theaterId
        );
    }

    public static TheaterEntity buildTheaterEntity (UUID theaterId) {
        return TheaterEntity.builder()
                .id(theaterId)
                .name("My cinema")
                .email("cinema@cinema.io")
                .phone("+1 1234567890")
                .addressId(UUID.randomUUID())
                .build();

    }

    public static Stream<Arguments> provideDatabaseErrors(){
        var cinemaException = new CinemaException("DB error during save", CinemaExceptionTypes.TECHNICAL_ERROR);
        var transactionError = new CannotCreateTransactionException("Connection lost");
        return Stream.of(
                Arguments.of(cinemaException, true),
                Arguments.of(transactionError, false)
        );
    }


}
