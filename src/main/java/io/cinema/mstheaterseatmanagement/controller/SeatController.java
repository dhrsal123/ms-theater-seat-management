package io.cinema.mstheaterseatmanagement.controller;

import io.cinema.domain.annotations.HasManagerRole;
import io.cinema.mstheaterseatmanagement.domain.dto.request.SeatRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.SeatResponseDto;
import io.cinema.mstheaterseatmanagement.service.SeatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/theaters/{theaterId}/seats")
public class SeatController {

    private final SeatService seatService;

    @GetMapping
    public Flux<SeatResponseDto> getAllSeats(
            @PathVariable("theaterId") @NotNull UUID theaterId,
            @RequestParam("roomId") @NotNull UUID roomId
    ) {
        return seatService.getAllSeats(theaterId, roomId);
    }

    @HasManagerRole
    @PostMapping
    public Flux<SeatResponseDto> createSeats(
            @PathVariable("theaterId") @NotNull UUID theaterId,
            @RequestBody @NotNull @Valid List<SeatRequestDto> seatRequestDto
    ) {
        return seatService.createSeats(theaterId, seatRequestDto);
    }

    @HasManagerRole
    @PutMapping("/{seatId}")
    public Mono<ResponseEntity<SeatResponseDto>> updateSeat(
            @PathVariable("theaterId") @NotNull UUID theaterId,
            @PathVariable("seatId") @NotNull UUID seatId,
            @RequestBody @NotNull @Valid SeatRequestDto seatRequestDto
    ) {
        return seatService.updateSeat(theaterId, seatId, seatRequestDto)
                .map(ResponseEntity::ok);
    }

    @HasManagerRole
    @DeleteMapping("/{seatId}")
    public Mono<ResponseEntity<Void>> deleteSeat(
            @PathVariable("theaterId") @NotNull UUID theaterId,
            @PathVariable("seatId") @NotNull UUID seatId
    ) {
        return seatService.deleteSeat(theaterId, seatId)
                .thenReturn(ResponseEntity.noContent().build());
    }
}