package io.cinema.mstheaterseatmanagement.factory;

import io.cinema.domain.enumerated.CinemaExceptionTypes;
import io.cinema.domain.exceptions.CinemaException;
import io.cinema.mstheaterseatmanagement.domain.dto.request.TheaterRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.TheaterResponseDto;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterEntity;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterRowProjection;
import lombok.experimental.UtilityClass;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.transaction.CannotCreateTransactionException;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@UtilityClass
public class TheaterMockFactory {

    public static TheaterRowProjection buildTheaterRowProjection(UUID theaterId) {
        return new TheaterRowProjection(
                theaterId,
                "My cinema",
                "cinema@cinema.io",
                "+11234567890",
                "St 123 Av Heaven",
                "New York",
                "Tolima",
                "China",
                "123456",
                DayOfWeek.FRIDAY,
                LocalTime.NOON,
                LocalTime.MIDNIGHT
        );
    }

    public static TheaterResponseDto buildTheaterResponseDto(UUID theaterId) {
        var address = "St 123 Av Heaven, New York, Tolima, China, ZIP Code: 123456";
        var operatingHour = OperatingHoursMockFactory.buildOperatingHoursResponseDto();
        var operatingHours = List.of(operatingHour);

        return new TheaterResponseDto(
                theaterId.toString(),
                "My cinema",
                "cinema@cinema.io",
                "+11234567890",
                address,
                operatingHours
        );
    }

    public static TheaterRequestDto buildTheaterRequestDto() {
        var address = AddressMockFactory.buildAddressRequestDto();
        var operatingHours = OperatingHoursMockFactory.buildOperatingHoursRequestDto();

        return new TheaterRequestDto(
                "My cinema",
                "+11234567890",
                "cinema@cinema.io",
                address,
                operatingHours
        );

    }

    public static TheaterEntity buildTheaterEntity(UUID theaterId, UUID addressId) {
        return TheaterEntity.builder()
                .id(theaterId)
                .name("My cinema")
                .email("cinema@cinema.io")
                .phone("+11234567890")
                .addressId(addressId)
                .build();

    }

    public static java.util.stream.Stream<Arguments> provideDatabaseErrors() {
        var cinemaException = new CinemaException("DB error during save", CinemaExceptionTypes.TECHNICAL_ERROR);
        var transactionError = new CannotCreateTransactionException("Connection lost");
        return Stream.of(
                Arguments.of(cinemaException, true),
                Arguments.of(transactionError, false)
        );
    }
}
