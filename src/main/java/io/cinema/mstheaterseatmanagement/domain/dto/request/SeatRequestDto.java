package io.cinema.mstheaterseatmanagement.domain.dto.request;

import io.cinema.mstheaterseatmanagement.domain.enumerated.SeatStatus;
import io.cinema.mstheaterseatmanagement.domain.enumerated.SeatTypes;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record SeatRequestDto(
        @NotNull
        @PositiveOrZero(message = "The price increment must be greater than or equal to zero.")
        Double priceIncrement,

        @NotNull
        @Positive(message = "The row number must be greater than zero.")
        Integer rowNumber,

        @NotNull
        @Positive(message = "The column number must be greater than zero.")
        Integer colNumber,

        @NotNull(message = "The seat status must be valid.")
        SeatStatus seatStatus,

        @NotNull(message = "The seat type must be valid.")
        SeatTypes seatType,

        @NotNull(message = "The room id must be valid.")
        UUID roomId
) {

}
