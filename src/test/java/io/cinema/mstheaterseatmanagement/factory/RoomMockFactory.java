package io.cinema.mstheaterseatmanagement.factory;

import io.cinema.mstheaterseatmanagement.domain.dto.request.RoomRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.RoomResponseDto;
import io.cinema.mstheaterseatmanagement.domain.entity.RoomEntity;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class RoomMockFactory {
    public static RoomResponseDto buildRoomResponseDto(UUID roomId, UUID theaterId) {
        return new RoomResponseDto(
                roomId,
                "Room VIP - test",
                theaterId
        );
    }

    public static RoomRequestDto buildRoomRequestDto() {
        return new RoomRequestDto("Room VIP - test");
    }

    public static RoomEntity buildRoomEntity(UUID roomId, UUID theaterId) {
        return new RoomEntity(
                roomId,
                "Room VIP - test",
                theaterId
        );
    }

}
