package io.cinema.mstheaterseatmanagement.service.impl;

import io.cinema.domain.exceptions.CinemaException;
import io.cinema.mstheaterseatmanagement.domain.dto.request.RoomRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.RoomResponseDto;
import io.cinema.mstheaterseatmanagement.domain.entity.RoomEntity;
import io.cinema.mstheaterseatmanagement.mapper.RoomMapper;
import io.cinema.mstheaterseatmanagement.repository.RoomRepository;
import io.cinema.mstheaterseatmanagement.repository.SeatRepository;
import io.cinema.mstheaterseatmanagement.repository.TheaterRepository;
import io.cinema.mstheaterseatmanagement.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static io.cinema.domain.enumerated.CinemaExceptionTypes.BAD_REQUEST;
import static io.cinema.domain.enumerated.CinemaExceptionTypes.NOT_FOUND;
import static io.cinema.domain.enumerated.CinemaExceptionTypes.TECHNICAL_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final TheaterRepository theaterRepository;
    private final TransactionalOperator transactionalOperator;
    private final SeatRepository seatRepository;
    private final RoomMapper roomMapper;

    @Override
    public Flux<RoomResponseDto> getAllRooms(UUID theaterId) {
        return roomRepository.findRoomEntityByTheaterId(theaterId)
                .map(roomMapper::toResponseDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to find rooms for theater {}: {}", theaterId, e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during read", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<RoomResponseDto> getRoomById(UUID theaterId, UUID roomId) {
        return roomRepository.findById(roomId)
                .<RoomResponseDto>handle((roomEntity, sink) -> {
                    if (!theaterId.equals(roomEntity.getTheaterId())) {
                        sink.error(new CinemaException("The Room specified does not belong to the theater.", BAD_REQUEST));
                        return;
                    }
                    sink.next(roomMapper.toResponseDto(roomEntity));
                })
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to find room by id {}: {}", roomId, e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during read", TECHNICAL_ERROR)
                );
    }

    @Override
    public Flux<RoomResponseDto> saveRooms(UUID theaterId, List<RoomRequestDto> roomRequestDtos) {
        return theaterRepository
                .findById(theaterId)
                .switchIfEmpty(Mono.error(new CinemaException("Theater not found.", NOT_FOUND)))
                .flatMapMany(theaterEntity -> {
                    var roomEntities = roomRequestDtos.stream()
                            .map(dto -> roomMapper.toEntity(dto, theaterId))
                            .toList();

                    return roomRepository.saveAll(roomEntities)
                            .map(roomMapper::toResponseDto);
                })
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to save rooms for theater {}: {}", theaterId, e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during save", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<RoomResponseDto> updateRoom(UUID theaterId, UUID roomId, RoomRequestDto roomRequestDto) {
        return getAndValidateRoomBelongsToTheater(theaterId, roomId)
                .flatMap(room -> {
                    roomMapper.updateEntityFromDto(roomRequestDto, room);
                    return roomRepository.save(room)
                            .map(roomMapper::toResponseDto);
                })
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to update room {}: {}", roomId, e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during update", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<Void> deleteRoom(UUID theaterId, UUID roomId) {
        return getAndValidateRoomBelongsToTheater(theaterId, roomId)
                .flatMap(roomEntity ->
                        seatRepository.deleteAllByRoomId(roomId)
                                .then(roomRepository.deleteById(roomId))
                )
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to delete room {}: {}", roomId, e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during deletion", TECHNICAL_ERROR)
                );
    }

    //private methods
    private Mono<RoomEntity> getAndValidateRoomBelongsToTheater(UUID theaterId, UUID roomId) {
        return roomRepository.findById(roomId)
                .switchIfEmpty(Mono.error(new CinemaException("Room not found.", NOT_FOUND)))
                .flatMap(room -> {
                    if (!room.getTheaterId().equals(theaterId)) {
                        return Mono.error(new CinemaException(
                                "Room does not belong to the specified theater",
                                BAD_REQUEST
                        ));
                    }
                    return Mono.just(room);
                });
    }
}