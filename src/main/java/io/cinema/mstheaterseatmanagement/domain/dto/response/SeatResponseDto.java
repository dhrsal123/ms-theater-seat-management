package io.cinema.mstheaterseatmanagement.domain.dto.response;

import io.cinema.mstheaterseatmanagement.domain.enumerated.SeatStatus;
import io.cinema.mstheaterseatmanagement.domain.enumerated.SeatTypes;

import java.util.UUID;

public record SeatResponseDto(
        UUID seatId,

        Double priceIncrement,

        Integer rowNumber,
        Integer colNumber,

        SeatStatus seatStatus,
        SeatTypes seatType,

        UUID roomId
) {

}
