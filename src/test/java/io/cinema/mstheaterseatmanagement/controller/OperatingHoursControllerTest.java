package io.cinema.mstheaterseatmanagement.controller;

import io.cinema.mstheaterseatmanagement.domain.dto.request.OperatingHoursRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.OperatingHoursInfoResponseDto;
import io.cinema.mstheaterseatmanagement.service.OperatingHoursService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.cinema.mstheaterseatmanagement.factory.MockFactory.buildOperatingHoursInfoResponseDto;
import static io.cinema.mstheaterseatmanagement.factory.MockFactory.buildOperatingHoursRequestDto;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OperatingHoursControllerTest {
    private WebTestClient webTestClient;
    private OperatingHoursService operatingHoursService;

    @BeforeEach
    void setUp() {
        operatingHoursService = mock(OperatingHoursService.class);
        webTestClient = WebTestClient.bindToController(new OperatingHoursController(operatingHoursService)).build();
    }

    @Test
    void shouldGetOperatingHours() {
        // given
        var operatingHour = buildOperatingHoursInfoResponseDto();

        var operatingHours = Flux.just(operatingHour);
        var theaterId = UUID.randomUUID();

        // when
        when(operatingHoursService.getTheaterOperatingHours(theaterId))
                .thenReturn(operatingHours);

        // then
        webTestClient.get().uri("/api/v1/theaters/{theaterId}/operating-hours", theaterId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(OperatingHoursInfoResponseDto.class).contains(operatingHour);

        verify(operatingHoursService).getTheaterOperatingHours(theaterId);
    }


    @Test
    void shouldSaveOperatingHours() {
        // given
        var operatingHourRequest = buildOperatingHoursRequestDto();
        var operatingHour = buildOperatingHoursInfoResponseDto();
        var operatingHours = Flux.just(operatingHour);
        var theaterId = UUID.randomUUID();

        // when
        when(operatingHoursService.saveTheaterOperatingHours(theaterId, operatingHourRequest))
                .thenReturn(operatingHours);

        // then
        webTestClient.post().uri("/api/v1/theaters/{theaterId}/operating-hours", theaterId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(operatingHourRequest), OperatingHoursRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(OperatingHoursInfoResponseDto.class).contains(operatingHour);

        verify(operatingHoursService).saveTheaterOperatingHours(theaterId, operatingHourRequest);
    }

    @Test
    void shouldUpdateOperatingHours() {
        // given
        var operatingHoursRequest = buildOperatingHoursRequestDto();
        var operatingHourRequest = operatingHoursRequest.getFirst();

        var operatingHour = buildOperatingHoursInfoResponseDto();
        var operatingHoursId = operatingHour.operatingHoursId();
        var operatingHours = Mono.just(operatingHour);
        var theaterId = UUID.randomUUID();

        // when
        when(operatingHoursService.updateOperatingHours(theaterId, operatingHoursId, operatingHourRequest))
                .thenReturn(operatingHours);

        // then
        webTestClient.put().uri(
                        "/api/v1/theaters/{theaterId}/operating-hours/{operatingHoursId}",
                        theaterId,
                        operatingHoursId
                )
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(operatingHourRequest), OperatingHoursRequestDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(OperatingHoursInfoResponseDto.class).contains(operatingHour);

        verify(operatingHoursService).updateOperatingHours(theaterId, operatingHoursId, operatingHourRequest);
    }

    @Test
    void shouldDeleteOperatingHours() {
        // given
        var theaterId = UUID.randomUUID();
        var operatingHoursId = UUID.randomUUID();

        // when
        when(operatingHoursService.deleteOperatingHours(theaterId, operatingHoursId))
                .thenReturn(Mono.empty());

        // then
        webTestClient.delete().uri(
                        "/api/v1/theaters/{theaterId}/operating-hours/{operatingHoursId}",
                        theaterId,
                        operatingHoursId
                )
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);

        verify(operatingHoursService).deleteOperatingHours(theaterId, operatingHoursId);
    }
}