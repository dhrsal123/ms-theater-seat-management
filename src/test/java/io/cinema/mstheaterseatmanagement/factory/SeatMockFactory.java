package io.cinema.mstheaterseatmanagement.factory;

import io.cinema.mstheaterseatmanagement.domain.dto.request.SeatRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.SeatResponseDto;
import io.cinema.mstheaterseatmanagement.domain.entity.SeatEntity;
import io.cinema.mstheaterseatmanagement.domain.enumerated.SeatStatus;
import io.cinema.mstheaterseatmanagement.domain.enumerated.SeatTypes;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class SeatMockFactory {
    public static SeatResponseDto buildSeatResponseDto(UUID seatId, UUID roomId) {
        return new SeatResponseDto(
                seatId,
                Double.valueOf("10.1"),
                1,
                1,
                SeatStatus.OPERATIONAL,
                SeatTypes.PREMIUM,
                roomId
        );
    }


    public static SeatRequestDto buildSeatRequestDto(UUID roomId) {
        return new SeatRequestDto(
                Double.valueOf("10.1"),
                1,
                1,
                SeatStatus.OPERATIONAL,
                SeatTypes.PREMIUM,
                roomId
        );
    }

    public static SeatEntity buildSeatEntity(UUID seatId, UUID roomId) {
        return new SeatEntity(
                seatId,
                Double.valueOf("10.1"),
                1,
                1,
                SeatStatus.OPERATIONAL,
                SeatTypes.PREMIUM,
                roomId
        );
    }
}
