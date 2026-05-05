package io.cinema.mstheaterseatmanagement.service.impl;

import io.cinema.domain.exceptions.CinemaException;
import io.cinema.mstheaterseatmanagement.domain.dto.request.SeatRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.SeatResponseDto;
import io.cinema.mstheaterseatmanagement.mapper.SeatMapper;
import io.cinema.mstheaterseatmanagement.repository.RoomRepository;
import io.cinema.mstheaterseatmanagement.repository.SeatRepository;
import io.cinema.mstheaterseatmanagement.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static io.cinema.domain.enumerated.CinemaExceptionTypes.BAD_REQUEST;
import static io.cinema.domain.enumerated.CinemaExceptionTypes.TECHNICAL_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final RoomRepository roomRepository;
    private final SeatMapper seatMapper;
    private final TransactionalOperator transactionalOperator;

    @Override
    public Flux<SeatResponseDto> getAllSeats(UUID theaterId, UUID roomId) {
        return validateRoomBelongsToTheater(theaterId, roomId)
                .thenMany(seatRepository.findAllByRoomId(roomId))
                .map(seatMapper::toResponseDto)
                .doOnError(e -> log.error("Failed to fetch seats for room {}: {}", roomId, e.getMessage()))
                .onErrorMap(e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error fetching seats", TECHNICAL_ERROR));
    }

    @Override
    public Flux<SeatResponseDto> createSeats(UUID theaterId, List<SeatRequestDto> seatRequestDtos) {
        return Flux.fromIterable(seatRequestDtos)
                .flatMap(dto -> validateRoomBelongsToTheater(theaterId, dto.roomId())
                        .thenReturn(dto)
                )
                .map(seatMapper::toEntity)
                .collectList()
                .flatMapMany(seatRepository::saveAll)
                .map(seatMapper::toResponseDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to create seats in bulk: {}", e.getMessage()))
                .onErrorMap(e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error creating seats", TECHNICAL_ERROR));
    }

    @Override
    public Mono<SeatResponseDto> updateSeat(UUID theaterId, UUID seatId, SeatRequestDto dto) {
        return seatRepository.findById(seatId)
                .switchIfEmpty(Mono.error(new CinemaException("Seat not found", BAD_REQUEST)))
                .flatMap(existingSeat -> validateRoomBelongsToTheater(theaterId, dto.roomId())
                        .then(Mono.defer(() -> {
                            seatMapper.updateEntityFromDto(dto, existingSeat);
                            return seatRepository.save(existingSeat);
                        }))
                )
                .map(seatMapper::toResponseDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to update seat {}: {}", seatId, e.getMessage()))
                .onErrorMap(e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error updating seat", TECHNICAL_ERROR));
    }

    @Override
    public Mono<Void> deleteSeat(UUID theaterId, UUID seatId) {
        return seatRepository.findById(seatId)
                .switchIfEmpty(Mono.error(new CinemaException("Seat not found", BAD_REQUEST)))
                .flatMap(seat -> validateRoomBelongsToTheater(theaterId, seat.getRoomId())
                        .then(seatRepository.deleteById(seatId))
                )
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to delete seat {}: {}", seatId, e.getMessage()))
                .onErrorMap(e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error deleting seat", TECHNICAL_ERROR));
    }

    // private methods
    private Mono<Void> validateRoomBelongsToTheater(UUID theaterId, UUID roomId) {
        return roomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(new CinemaException("Room not found", BAD_REQUEST)))
                .flatMap(room -> {
                    if (!room.getTheaterId().equals(theaterId)) {
                        return Mono.error(new CinemaException(
                                "Room does not belong to the specified theater",
                                BAD_REQUEST
                        ));
                    }
                    return Mono.empty();
                });
    }
}