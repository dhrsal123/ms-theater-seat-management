package io.cinema.mstheaterseatmanagement.controller;

import io.cinema.mstheaterseatmanagement.domain.dto.request.TheaterRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.TheaterResponseDto;
import io.cinema.mstheaterseatmanagement.factory.TheaterMockFactory;
import io.cinema.mstheaterseatmanagement.service.TheaterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TheaterControllerTest {

    @Mock
    private TheaterService theaterService;

    @InjectMocks
    private TheaterController theaterController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(theaterController).build();
    }

    @Test
    void shouldGetAllTheaters() {
        UUID theaterId = UUID.randomUUID();

        TheaterResponseDto responseDto = TheaterMockFactory.buildTheaterResponseDto(theaterId);

        when(theaterService.getAllTheaters(anyInt(), anyInt())).thenReturn(Flux.just(responseDto));

        webTestClient.get()
                .uri("/api/v1/theaters?page=0&size=10")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo(responseDto.name());
    }

    @Test
    void shouldGetTheaterById() {
        UUID theaterId = UUID.randomUUID();

        TheaterResponseDto responseDto = TheaterMockFactory.buildTheaterResponseDto(theaterId);

        when(theaterService.getTheaterById(theaterId)).thenReturn(Mono.just(responseDto));

        webTestClient.get()
                .uri("/api/v1/theaters/{theaterId}", theaterId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(responseDto.name())
                .jsonPath("$.theaterId").isEqualTo(theaterId.toString());
    }

    @Test
    void shouldReturnNotFoundWhenTheaterDoesNotExist() {
        UUID theaterId = UUID.randomUUID();

        when(theaterService.getTheaterById(theaterId)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/v1/theaters/{theaterId}", theaterId)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldCreateTheater() {
        UUID theaterId = UUID.randomUUID();
        TheaterRequestDto requestDto = TheaterMockFactory.buildTheaterRequestDto();

        TheaterResponseDto responseDto = TheaterMockFactory.buildTheaterResponseDto(theaterId);

        when(theaterService.createTheater(any(TheaterRequestDto.class))).thenReturn(Mono.just(responseDto));

        webTestClient.post()
                .uri("/api/v1/theaters")
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(responseDto.name());
    }

    @Test
    void shouldUpdateTheater() {
        UUID theaterId = UUID.randomUUID();
        TheaterRequestDto requestDto = TheaterMockFactory.buildTheaterRequestDto();

        TheaterResponseDto responseDto = TheaterMockFactory.buildTheaterResponseDto(theaterId);

        when(theaterService.updateTheater(eq(theaterId), any(TheaterRequestDto.class))).thenReturn(Mono.just(responseDto));

        webTestClient.put()
                .uri("/api/v1/theaters/{theaterId}", theaterId)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(responseDto.name());
    }

    @Test
    void shouldDeleteTheater() {
        UUID theaterId = UUID.randomUUID();

        when(theaterService.deleteTheater(theaterId)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/theaters/{theaterId}", theaterId)
                .exchange()
                .expectStatus().isNoContent();
    }
}