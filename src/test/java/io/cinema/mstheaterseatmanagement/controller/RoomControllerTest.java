package io.cinema.mstheaterseatmanagement.controller;

import io.cinema.mstheaterseatmanagement.domain.dto.request.RoomRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.RoomResponseDto;
import io.cinema.mstheaterseatmanagement.factory.RoomMockFactory;
import io.cinema.mstheaterseatmanagement.service.RoomService;
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

class RoomControllerTest {


    private RoomService roomService;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        this.roomService = mock(RoomService.class);
        this.webTestClient = WebTestClient.bindToController(new RoomController(roomService)).build();
    }

    @Test
    void shouldGetAllRooms() {
        // given
        var roomId = UUID.randomUUID();
        var theaterId = UUID.randomUUID();

        RoomResponseDto roomResponseDto = RoomMockFactory.buildRoomResponseDto(roomId, theaterId);
        // when
        when(roomService.getAllRooms(theaterId)).thenReturn(Flux.just(roomResponseDto));

        // then
        webTestClient.get()
                .uri("/api/v1/theaters/{theaterId}/rooms", theaterId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RoomResponseDto.class).contains(roomResponseDto);

        verify(roomService).getAllRooms(theaterId);
    }

    @Test
    void shouldSaveRooms() {
        // given
        var roomRequest = RoomMockFactory.buildRoomRequestDto();
        var roomRequests = List.of(roomRequest);

        var roomId = UUID.randomUUID();
        var theaterId = UUID.randomUUID();

        RoomResponseDto roomResponseDto = RoomMockFactory.buildRoomResponseDto(roomId, theaterId);
        // when
        when(roomService.saveRooms(theaterId, roomRequests)).thenReturn(Flux.just(roomResponseDto));

        // then
        webTestClient.post()
                .uri("/api/v1/theaters/{theaterId}/rooms", theaterId)
                .body(Mono.just(roomRequests), RoomRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RoomResponseDto.class).contains(roomResponseDto);

        verify(roomService).saveRooms(theaterId, roomRequests);
    }


    @Test
    void shouldUpdateRoom() {
        // given
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var roomRequest = RoomMockFactory.buildRoomRequestDto();
        var roomResponse = RoomMockFactory.buildRoomResponseDto(theaterId, roomId);


        //when
        when(roomService.updateRoom(theaterId, roomId, roomRequest)).thenReturn(Mono.just(roomResponse));

        //then
        webTestClient.put()
                .uri("/api/v1/theaters/{theaterId}/rooms/{roomId}", theaterId, roomId)
                .body(Mono.just(roomRequest), RoomRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RoomResponseDto.class).isEqualTo(roomResponse);

        verify(roomService).updateRoom(theaterId, roomId, roomRequest);
    }

    @Test
    void shouldDeleteRoom() {
        // given
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();

        //when
        when(roomService.deleteRoom(theaterId, roomId)).thenReturn(Mono.empty());

        //then
        webTestClient.delete()
                .uri("/api/v1/theaters/{theaterId}/rooms/{roomId}", theaterId, roomId)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);

        verify(roomService).deleteRoom(theaterId, roomId);
    }
}