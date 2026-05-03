package io.cinema.mstheaterseatmanagement.service;

import io.cinema.mstheaterseatmanagement.domain.dto.request.SeatRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.SeatResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface SeatService {

    Flux<SeatResponseDto> getAllSeats(UUID theaterId, UUID roomId);

    Flux<SeatResponseDto> createSeats(UUID theaterId, List<SeatRequestDto> seatRequestDtos);

    Mono<SeatResponseDto> updateSeat(UUID theaterId, UUID seatId, SeatRequestDto seatRequestDto);

    Mono<Void> deleteSeat(UUID theaterId, UUID seatId);
}