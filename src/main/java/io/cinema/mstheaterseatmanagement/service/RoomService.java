package io.cinema.mstheaterseatmanagement.service;

import io.cinema.mstheaterseatmanagement.domain.dto.request.RoomRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.RoomResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface RoomService {
    Flux<RoomResponseDto> getAllRooms(UUID theaterId);

    Mono<RoomResponseDto> getRoomById(UUID theaterId, UUID roomId);

    Flux<RoomResponseDto> saveRooms(UUID theaterId, List<RoomRequestDto> roomRequestDtos);

    Mono<RoomResponseDto> updateRoom(UUID theaterId, UUID roomId, RoomRequestDto roomRequestDto);

    Mono<Void> deleteRoom(UUID theaterId, UUID roomId);
}