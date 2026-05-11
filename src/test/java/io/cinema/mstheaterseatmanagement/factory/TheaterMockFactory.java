package io.cinema.mstheaterseatmanagement.factory;

import io.cinema.domain.enumerated.CinemaExceptionTypes;
import io.cinema.domain.exceptions.CinemaException;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterEntity;
import lombok.experimental.UtilityClass;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.transaction.CannotCreateTransactionException;

import java.util.UUID;
import java.util.stream.Stream;

@UtilityClass
public class TheaterMockFactory {

    public static TheaterEntity buildTheaterEntity(UUID theaterId) {
        return TheaterEntity.builder()
                .id(theaterId)
                .name("My cinema")
                .email("cinema@cinema.io")
                .phone("+1 1234567890")
                .addressId(UUID.randomUUID())
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
