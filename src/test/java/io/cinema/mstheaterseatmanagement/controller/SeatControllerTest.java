package io.cinema.mstheaterseatmanagement.controller;

import io.cinema.mstheaterseatmanagement.domain.dto.response.SeatResponseDto;
import io.cinema.mstheaterseatmanagement.factory.SeatMockFactory;
import io.cinema.mstheaterseatmanagement.service.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SeatControllerTest {

    private SeatService seatService;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        this.seatService = mock(SeatService.class);
        this.webTestClient = WebTestClient.bindToController(new SeatController(seatService)).build();
    }

    @Test
    void shouldGetAllSeats() {
        // given
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var seatId = UUID.randomUUID();
        var seatResponseDto = SeatMockFactory.buildSeatResponseDto(seatId, roomId);

        // when
        when(seatService.getAllSeats(theaterId, roomId)).thenReturn(Flux.just(seatResponseDto));

        // then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/theaters/{theaterId}/rooms/{roomId}/seats")
                        .build(theaterId, roomId))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SeatResponseDto.class).contains(seatResponseDto);

        verify(seatService).getAllSeats(theaterId, roomId);
    }

    @Test
    void shouldCreateSeats() {
        // given
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var seatRequest = SeatMockFactory.buildSeatRequestDto();
        var seatRequests = List.of(seatRequest);
        var seatId = UUID.randomUUID();
        var seatResponseDto = SeatMockFactory.buildSeatResponseDto(seatId, roomId);

        // when
        when(seatService.createSeats(theaterId, roomId, seatRequests)).thenReturn(Flux.just(seatResponseDto));

        // then
        webTestClient.post()
                .uri("/api/v1/theaters/{theaterId}/rooms/{roomId}/seats", theaterId, roomId)
                .bodyValue(seatRequests)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SeatResponseDto.class).contains(seatResponseDto);

        verify(seatService).createSeats(theaterId, roomId, seatRequests);
    }

    @Test
    void shouldUpdateSeat() {
        // given
        var theaterId = UUID.randomUUID();
        var seatId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var seatRequest = SeatMockFactory.buildSeatRequestDto();
        var seatResponse = SeatMockFactory.buildSeatResponseDto(seatId, roomId);

        // when
        when(seatService.updateSeat(theaterId, roomId, seatId, seatRequest)).thenReturn(Mono.just(seatResponse));

        // then
        webTestClient.put()
                .uri("/api/v1/theaters/{theaterId}/rooms/{roomId}/seats/{seatId}", theaterId, roomId, seatId)
                .bodyValue(seatRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SeatResponseDto.class).isEqualTo(seatResponse);

        verify(seatService).updateSeat(theaterId, roomId, seatId, seatRequest);
    }

    @Test
    void shouldDeleteSeat() {
        // given
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var seatId = UUID.randomUUID();

        // when
        when(seatService.deleteSeat(theaterId, roomId, seatId)).thenReturn(Mono.empty());

        // then
        webTestClient.delete()
                .uri("/api/v1/theaters/{theaterId}/rooms/{roomId}/seats/{seatId}", theaterId, roomId, seatId)
                .exchange()
                .expectStatus().isNoContent();

        verify(seatService).deleteSeat(theaterId, roomId, seatId);
    }
}