package io.cinema.mstheaterseatmanagement.domain.dto.response;

import java.util.UUID;

public record RoomResponseDto(
        UUID id,
        String name,
        UUID theaterId
) {
}
