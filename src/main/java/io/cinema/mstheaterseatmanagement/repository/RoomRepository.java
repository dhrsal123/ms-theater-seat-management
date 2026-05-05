package io.cinema.mstheaterseatmanagement.repository;

import io.cinema.mstheaterseatmanagement.domain.entity.RoomEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface RoomRepository extends ReactiveCrudRepository<RoomEntity, UUID> {
    Flux<RoomEntity> findRoomEntityByTheaterId(UUID theaterId);
}
