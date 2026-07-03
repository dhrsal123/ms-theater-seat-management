package io.cinema.mstheaterseatmanagement.controller;

import io.cinema.domain.annotations.HasManagerRole;
import io.cinema.mstheaterseatmanagement.domain.dto.request.RoomRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.RoomResponseDto;
import io.cinema.mstheaterseatmanagement.service.RoomService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/theaters/{theaterId}/rooms")
public class RoomController {
    private final RoomService roomService;

    @Cacheable(value = "rooms", key = "#theaterId")
    @GetMapping
    public Flux<RoomResponseDto> getAllRooms(@PathVariable("theaterId") @NotNull UUID theaterId) {
        return roomService.getAllRooms(theaterId);
    }

    @Cacheable(value = "room", key = "#roomId")
    @GetMapping("/{roomId}")
    public Mono<RoomResponseDto> getRoomById(
            @PathVariable("theaterId") @NotNull UUID theaterId,
            @PathVariable("roomId") @NotNull UUID roomId
    ) {
        return roomService.getRoomById(theaterId, roomId);
    }

    @HasManagerRole
    @CacheEvict(value = "rooms", key = "#theaterId")
    @PostMapping
    public Flux<RoomResponseDto> saveRooms(
            @PathVariable("theaterId") @NotNull UUID theaterId,
            @RequestBody @Valid List<RoomRequestDto> roomRequestDtos
    ) {
        return roomService.saveRooms(theaterId, roomRequestDtos);
    }

    @HasManagerRole
    @CacheEvict(value = "rooms", key = "#theaterId")
    @PutMapping("/{roomId}")
    public Mono<ResponseEntity<RoomResponseDto>> updateRoom(
            @PathVariable("theaterId") @NotNull UUID theaterId,
            @PathVariable("roomId") @NotNull UUID roomId,
            @RequestBody @Valid RoomRequestDto roomRequestDto
    ) {
        return roomService.updateRoom(theaterId, roomId, roomRequestDto)
                .map(ResponseEntity::ok);
    }

    @HasManagerRole
    @CacheEvict(value = "rooms", key = "#theaterId")
    @DeleteMapping("/{roomId}")
    public Mono<ResponseEntity<Void>> deleteRoom(
            @PathVariable("theaterId") @NotNull UUID theaterId,
            @PathVariable("roomId") @NotNull UUID roomId
    ) {
        return roomService.deleteRoom(theaterId, roomId)
                .thenReturn(ResponseEntity.noContent().build());
    }


}
