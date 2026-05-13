package io.cinema.mstheaterseatmanagement.service;

import io.cinema.domain.exceptions.CinemaException;
import io.cinema.mstheaterseatmanagement.domain.dto.response.OperatingHoursInfoResponseDto;
import io.cinema.mstheaterseatmanagement.domain.entity.OperatingHoursEntity;
import io.cinema.mstheaterseatmanagement.mapper.OperatingHoursMapperImpl;
import io.cinema.mstheaterseatmanagement.repository.OperatingHoursRepository;
import io.cinema.mstheaterseatmanagement.repository.TheaterRepository;
import io.cinema.mstheaterseatmanagement.service.impl.OperatingHoursServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.stream.Stream;

import static io.cinema.mstheaterseatmanagement.factory.OperatingHoursMockFactory.buildOperatingHoursEntity;
import static io.cinema.mstheaterseatmanagement.factory.OperatingHoursMockFactory.buildOperatingHoursInfoResponseDto;
import static io.cinema.mstheaterseatmanagement.factory.OperatingHoursMockFactory.buildOperatingHoursRequestDto;
import static io.cinema.mstheaterseatmanagement.factory.TheaterMockFactory.buildTheaterEntity;
import static io.cinema.mstheaterseatmanagement.factory.TheaterMockFactory.provideDatabaseErrors;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import({OperatingHoursMapperImpl.class})
class OperatingHoursServiceTest {

    @Autowired
    private OperatingHoursMapperImpl operatingHoursMapper;

    private TheaterRepository theaterRepository;
    private TransactionalOperator transactionalOperator;
    private OperatingHoursRepository operatingHoursRepository;
    private OperatingHoursService operatingHoursService;

    // private methods
    private static Stream<Arguments> provideDatabaseExceptions() {
        return provideDatabaseErrors();
    }

    @BeforeEach
    void setUp() {
        this.theaterRepository = mock(TheaterRepository.class);
        this.transactionalOperator = mock(TransactionalOperator.class);
        this.operatingHoursRepository = mock(OperatingHoursRepository.class);
        this.operatingHoursService = new OperatingHoursServiceImpl(
                theaterRepository,
                transactionalOperator,
                operatingHoursRepository,
                operatingHoursMapper
        );
    }

    @Test
    void shouldGetTheaterOperatingHours() {
        var theaterId = UUID.randomUUID();
        var operatingHourId = UUID.randomUUID();

        var operatingHour = buildOperatingHoursEntity(operatingHourId, theaterId);
        var operatingHoursInfoResponse = buildOperatingHoursInfoResponseDto(operatingHourId);

        when(operatingHoursRepository.findOperatingHoursByTheaterId(theaterId))
                .thenReturn(Flux.just(operatingHour));
        when(transactionalOperator.transactional(any(Flux.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Flux<OperatingHoursInfoResponseDto> response = operatingHoursService.getTheaterOperatingHours(theaterId);

        StepVerifier.create(response)
                .expectNext(operatingHoursInfoResponse)
                .verifyComplete();

        verify(transactionalOperator).transactional(any(Flux.class));
        verify(operatingHoursRepository).findOperatingHoursByTheaterId(theaterId);
    }

    @MethodSource(value = "provideDatabaseExceptions")
    @ParameterizedTest
    void shouldHandleDatabaseErrorsGetOperatingHours(Throwable inputException, boolean isCinemaException) {
        var theaterId = UUID.randomUUID();
        String expectedMessage = isCinemaException ? inputException.getMessage() : "DB error during read";

        when(operatingHoursRepository.findOperatingHoursByTheaterId(theaterId))
                .thenReturn(Flux.error(inputException));
        when(transactionalOperator.transactional(any(Flux.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Flux<OperatingHoursInfoResponseDto> response = operatingHoursService.getTheaterOperatingHours(theaterId);

        StepVerifier.create(response)
                .expectErrorMatches(error ->
                        error instanceof CinemaException && error.getMessage().equals(expectedMessage)
                )
                .verify();
    }

    @Test
    void shouldSaveOperatingHours() {
        var theaterId = UUID.randomUUID();
        var operatingHours = buildOperatingHoursRequestDto();
        var operatingHourId = UUID.randomUUID();
        var operatingHour = buildOperatingHoursInfoResponseDto(operatingHourId);
        var operatingHoursEntity = buildOperatingHoursEntity(operatingHourId, theaterId);
        var addressId = UUID.randomUUID();
        var theater = buildTheaterEntity(theaterId, addressId);

        when(theaterRepository.findById(theaterId)).thenReturn(Mono.just(theater));
        when(operatingHoursRepository.saveAll(anyIterable())).thenReturn(Flux.just(operatingHoursEntity));
        when(transactionalOperator.transactional(any(Flux.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var operatingHoursResponse = operatingHoursService.saveTheaterOperatingHours(theaterId, operatingHours);

        StepVerifier.create(operatingHoursResponse)
                .expectNext(operatingHour)
                .verifyComplete();

        verify(transactionalOperator).transactional(any(Flux.class));
        verify(theaterRepository).findById(theaterId);
        verify(operatingHoursRepository).saveAll(anyIterable());
    }

    @MethodSource(value = "provideDatabaseExceptions")
    @ParameterizedTest
    void shouldHandleDatabaseErrorsSaveOperatingHours(Throwable inputException, boolean isCinemaException) {
        var theaterId = UUID.randomUUID();
        var operatingHours = buildOperatingHoursRequestDto();
        String expectedMessage = isCinemaException ? inputException.getMessage() : "DB error during save";

        when(theaterRepository.findById(theaterId)).thenReturn(Mono.error(inputException));
        when(transactionalOperator.transactional(any(Flux.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = operatingHoursService.saveTheaterOperatingHours(theaterId, operatingHours);

        StepVerifier.create(response)
                .expectErrorMatches(error ->
                        error instanceof CinemaException && error.getMessage().equals(expectedMessage)
                )
                .verify();
    }

    @Test
    void shouldUpdateOperatingHours() {
        var operatingHourId = UUID.randomUUID();
        var theaterId = UUID.randomUUID();
        var operatingHoursRequest = buildOperatingHoursRequestDto().getFirst();

        var operatingHours = buildOperatingHoursEntity(operatingHourId, theaterId);
        var operatingHoursInfoResponse = buildOperatingHoursInfoResponseDto(operatingHourId);

        when(operatingHoursRepository.findById(operatingHourId)).thenReturn(Mono.just(operatingHours));
        when(operatingHoursRepository.save(any(OperatingHoursEntity.class))).thenReturn(Mono.just(operatingHours));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var operatingHoursResponse = operatingHoursService.updateOperatingHours(
                theaterId,
                operatingHourId,
                operatingHoursRequest
        );

        StepVerifier.create(operatingHoursResponse)
                .expectNext(operatingHoursInfoResponse)
                .verifyComplete();

        verify(transactionalOperator).transactional(any(Mono.class));
        verify(operatingHoursRepository).save(any(OperatingHoursEntity.class));
        verify(operatingHoursRepository, times(2)).findById(operatingHourId);
    }

    @Test
    void shouldThrowExceptionWhenOperatingHourNotFoundForUpdate() {
        var operatingHourId = UUID.randomUUID();
        var theaterId = UUID.randomUUID();
        var operatingHoursRequest = buildOperatingHoursRequestDto().getFirst();

        when(operatingHoursRepository.findById(operatingHourId)).thenReturn(Mono.empty());
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = operatingHoursService.updateOperatingHours(theaterId, operatingHourId, operatingHoursRequest);

        StepVerifier.create(response)
                .expectErrorMatches(error ->
                        error instanceof CinemaException && error.getMessage().equals("Operating hour not found")
                )
                .verify();
    }

    @Test
    void shouldThrowExceptionWhenTheaterIdIsNotTheSameForUpdate() {
        var operatingHourId = UUID.randomUUID();
        var originalTheaterId = UUID.randomUUID();
        var operatingHoursRequest = buildOperatingHoursRequestDto().getFirst();

        var operatingHours = buildOperatingHoursEntity(operatingHourId, originalTheaterId);
        var differentTheaterId = UUID.randomUUID();

        when(operatingHoursRepository.findById(operatingHourId)).thenReturn(Mono.just(operatingHours));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var operatingHoursResponse = operatingHoursService.updateOperatingHours(
                differentTheaterId,
                operatingHourId,
                operatingHoursRequest
        );

        StepVerifier.create(operatingHoursResponse)
                .expectErrorMatches(error ->
                        error instanceof CinemaException && error.getMessage()
                                .equals("Operating hours specified does not belong to the specified theater")
                )
                .verify();
    }

    @MethodSource(value = "provideDatabaseExceptions")
    @ParameterizedTest
    void shouldHandleDatabaseErrorsUpdateOperatingHours(Throwable inputException, boolean isCinemaException) {
        var theaterId = UUID.randomUUID();
        var operatingHourId = UUID.randomUUID();
        var operatingHoursRequest = buildOperatingHoursRequestDto().getFirst();
        var operatingHours = buildOperatingHoursEntity(operatingHourId, theaterId);

        String expectedMessage = isCinemaException ? inputException.getMessage() : "DB error during update";

        when(operatingHoursRepository.findById(operatingHourId)).thenReturn(Mono.just(operatingHours));
        when(operatingHoursRepository.save(any(OperatingHoursEntity.class))).thenReturn(Mono.error(inputException));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = operatingHoursService.updateOperatingHours(theaterId, operatingHourId, operatingHoursRequest);

        StepVerifier.create(response)
                .expectErrorMatches(error ->
                        error instanceof CinemaException && error.getMessage().equals(expectedMessage)
                )
                .verify();
    }

    @Test
    void shouldDeleteOperatingHours() {
        var theaterId = UUID.randomUUID();
        var operatingHourId = UUID.randomUUID();
        var operatingHours = buildOperatingHoursEntity(operatingHourId, theaterId);

        when(operatingHoursRepository.findById(operatingHourId)).thenReturn(Mono.just(operatingHours));
        when(operatingHoursRepository.deleteById(operatingHourId)).thenReturn(Mono.empty());
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = operatingHoursService.deleteOperatingHours(theaterId, operatingHourId);

        StepVerifier.create(response).verifyComplete();

        verify(operatingHoursRepository).findById(operatingHourId);
        verify(operatingHoursRepository).deleteById(operatingHourId);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldThrowExceptionWhenOperatingHourNotFoundForDelete() {
        var theaterId = UUID.randomUUID();
        var operatingHourId = UUID.randomUUID();

        when(operatingHoursRepository.findById(operatingHourId)).thenReturn(Mono.empty());
        when(operatingHoursRepository.deleteById(operatingHourId)).thenReturn(Mono.empty());
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = operatingHoursService.deleteOperatingHours(theaterId, operatingHourId);

        StepVerifier.create(response)
                .expectErrorMatches(error ->
                        error instanceof CinemaException && error.getMessage().equals("Operating hour not found")
                )
                .verify();
    }

    @Test
    void shouldThrowExceptionWhenTheaterIdIsNotTheSameForDelete() {
        var operatingHourId = UUID.randomUUID();
        var originalTheaterId = UUID.randomUUID();
        var differentTheaterId = UUID.randomUUID();
        var operatingHours = buildOperatingHoursEntity(operatingHourId, originalTheaterId);

        when(operatingHoursRepository.findById(operatingHourId)).thenReturn(Mono.just(operatingHours));
        when(operatingHoursRepository.deleteById(operatingHourId)).thenReturn(Mono.empty());
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = operatingHoursService.deleteOperatingHours(differentTheaterId, operatingHourId);

        StepVerifier.create(response)
                .expectErrorMatches(error ->
                        error instanceof CinemaException && error.getMessage()
                                .equals("Operating hours specified does not belong to the specified theater")
                )
                .verify();
    }

    @MethodSource(value = "provideDatabaseExceptions")
    @ParameterizedTest
    void shouldHandleDatabaseErrorsDeleteOperatingHours(Throwable inputException, boolean isCinemaException) {
        var theaterId = UUID.randomUUID();
        var operatingHourId = UUID.randomUUID();
        var operatingHours = buildOperatingHoursEntity(operatingHourId, theaterId);

        String expectedMessage = isCinemaException ? inputException.getMessage() : "DB error during delete";

        when(operatingHoursRepository.findById(operatingHourId)).thenReturn(Mono.just(operatingHours));
        when(operatingHoursRepository.deleteById(operatingHourId)).thenReturn(Mono.error(inputException));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = operatingHoursService.deleteOperatingHours(theaterId, operatingHourId);

        StepVerifier.create(response)
                .expectErrorMatches(error ->
                        error instanceof CinemaException && error.getMessage().equals(expectedMessage)
                )
                .verify();
    }
}