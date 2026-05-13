package io.cinema.mstheaterseatmanagement.service;

import io.cinema.domain.exceptions.CinemaException;
import io.cinema.mstheaterseatmanagement.factory.RoomMockFactory;
import io.cinema.mstheaterseatmanagement.factory.SeatMockFactory;
import io.cinema.mstheaterseatmanagement.mapper.SeatMapperImpl;
import io.cinema.mstheaterseatmanagement.repository.RoomRepository;
import io.cinema.mstheaterseatmanagement.repository.SeatRepository;
import io.cinema.mstheaterseatmanagement.service.impl.SeatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static io.cinema.domain.enumerated.CinemaExceptionTypes.BAD_REQUEST;
import static io.cinema.domain.enumerated.CinemaExceptionTypes.TECHNICAL_ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import({SeatMapperImpl.class})
class SeatServiceImplTest {
    private SeatRepository seatRepository;
    private RoomRepository roomRepository;
    private TransactionalOperator transactionalOperator;
    private SeatService seatService;

    @BeforeEach
    void setUp() {
        seatRepository = Mockito.mock(SeatRepository.class);
        roomRepository = Mockito.mock(RoomRepository.class);
        transactionalOperator = Mockito.mock(TransactionalOperator.class);
        var seatMapper = new SeatMapperImpl();
        seatService = new SeatServiceImpl(
                seatRepository,
                roomRepository,
                seatMapper,
                transactionalOperator
        );
        when(transactionalOperator.transactional(any(Flux.class)))
                .then(transactionalOperator -> transactionalOperator.getArgument(0));

        when(transactionalOperator.transactional(any(Mono.class)))
                .then(transactionalOperator -> transactionalOperator.getArgument(0));
    }

    @Test
    void shouldGetAllSeats() {
        var roomId = UUID.randomUUID();
        var theaterId = UUID.randomUUID();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, theaterId);

        var seatId = UUID.randomUUID();
        var seats = SeatMockFactory.buildSeatEntity(seatId, roomId);
        var seatResponse = SeatMockFactory.buildSeatResponseDto(seatId, roomId);

        when(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity));
        when(seatRepository.findAllByRoomId(roomId)).thenReturn(Flux.just(seats));

        var response = seatService.getAllSeats(theaterId, roomId);

        StepVerifier.create(response)
                .expectNext(seatResponse)
                .verifyComplete();

        verify(roomRepository).findById(roomId);
        verify(seatRepository).findAllByRoomId(roomId);
    }

    @Test
    void shouldCreateSeats() {
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, theaterId);

        var seatRequest = SeatMockFactory.buildSeatRequestDto(roomId);
        var seatRequests = List.of(seatRequest);
        var seatId = UUID.randomUUID();
        var seatEntity = SeatMockFactory.buildSeatEntity(seatId, roomId);

        var seatResponse = SeatMockFactory.buildSeatResponseDto(seatId, roomId);

        when(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity));
        when(seatRepository.saveAll(anyIterable())).thenReturn(Flux.just(seatEntity));

        var response = seatService.createSeats(theaterId, seatRequests);

        StepVerifier.create(response)
                .expectNext(seatResponse)
                .verifyComplete();

        verify(roomRepository).findById(roomId);
        verify(seatRepository).saveAll(anyIterable());
        verify(transactionalOperator).transactional(any(Flux.class));
    }

    @Test
    void shouldUpdateSeat() {
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, theaterId);

        var seatRequest = SeatMockFactory.buildSeatRequestDto(roomId);
        var seatId = UUID.randomUUID();
        var seatEntity = SeatMockFactory.buildSeatEntity(seatId, roomId);

        var seatResponse = SeatMockFactory.buildSeatResponseDto(seatId, roomId);

        when(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity));
        when(seatRepository.findById(seatId)).thenReturn(Mono.just(seatEntity));
        when(seatRepository.save(seatEntity)).thenReturn(Mono.just(seatEntity));

        var response = seatService.updateSeat(theaterId, seatId, seatRequest);

        StepVerifier.create(response)
                .expectNext(seatResponse)
                .verifyComplete();

        verify(roomRepository).findById(roomId);
        verify(seatRepository).save(seatEntity);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldDeleteSeat() {
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, theaterId);

        var seatId = UUID.randomUUID();
        var seatEntity = SeatMockFactory.buildSeatEntity(seatId, roomId);

        when(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity));
        when(seatRepository.findById(seatId)).thenReturn(Mono.just(seatEntity));
        when(seatRepository.deleteById(seatId)).thenReturn(Mono.empty());

        var response = seatService.deleteSeat(theaterId, seatId);

        StepVerifier.create(response)
                .verifyComplete();

        verify(roomRepository).findById(roomId);
        verify(seatRepository).deleteById(seatId);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldFailToGetAllSeatsWhenRoomNotFound() {
        var roomId = UUID.randomUUID();
        var theaterId = UUID.randomUUID();

        when(roomRepository.findById(roomId)).thenReturn(Mono.empty());

        var response = seatService.getAllSeats(theaterId, roomId);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof CinemaException &&
                        throwable.getMessage().equals("Room not found") &&
                        ((CinemaException) throwable).getExceptionType() == BAD_REQUEST)
                .verify();
    }

    @Test
    void shouldFailToGetAllSeatsWhenRoomBelongsToDifferentTheater() {
        var expectedTheaterId = UUID.randomUUID();
        var actualTheaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, actualTheaterId);

        when(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity));

        var response = seatService.getAllSeats(expectedTheaterId, roomId);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof CinemaException &&
                        throwable.getMessage().equals("Room does not belong to the specified theater") &&
                        ((CinemaException) throwable).getExceptionType() == BAD_REQUEST)
                .verify();
    }

    @Test
    void shouldFailToUpdateSeatWhenSeatNotFound() {
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var seatId = UUID.randomUUID();
        var seatRequest = SeatMockFactory.buildSeatRequestDto(roomId);

        when(seatRepository.findById(seatId)).thenReturn(Mono.empty());

        var response = seatService.updateSeat(theaterId, seatId, seatRequest);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof CinemaException &&
                        throwable.getMessage().equals("Seat not found") &&
                        ((CinemaException) throwable).getExceptionType() == BAD_REQUEST)
                .verify();
    }

    @Test
    void shouldFailToDeleteSeatWhenSeatNotFound() {
        var theaterId = UUID.randomUUID();
        var seatId = UUID.randomUUID();

        when(seatRepository.findById(seatId)).thenReturn(Mono.empty());

        var response = seatService.deleteSeat(theaterId, seatId);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof CinemaException &&
                        throwable.getMessage().equals("Seat not found") &&
                        ((CinemaException) throwable).getExceptionType() == BAD_REQUEST)
                .verify();
    }

    @Test
    void shouldMapErrorOnGetAllSeats() {
        var roomId = UUID.randomUUID();
        var theaterId = UUID.randomUUID();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, theaterId);

        when(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity));
        when(seatRepository.findAllByRoomId(roomId)).thenReturn(Flux.error(new RuntimeException("DB Connection Refused")));

        var response = seatService.getAllSeats(theaterId, roomId);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof CinemaException &&
                        throwable.getMessage().equals("DB error fetching seats") &&
                        ((CinemaException) throwable).getExceptionType() == TECHNICAL_ERROR)
                .verify();
    }

    @Test
    void shouldMapErrorOnCreateSeats() {
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, theaterId);

        var seatRequest = SeatMockFactory.buildSeatRequestDto(roomId);
        var seatRequests = List.of(seatRequest);

        when(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity));
        when(seatRepository.saveAll(anyIterable())).thenReturn(Flux.error(new RuntimeException("DB Save Failure")));

        var response = seatService.createSeats(theaterId, seatRequests);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof CinemaException &&
                        throwable.getMessage().equals("DB error creating seats") &&
                        ((CinemaException) throwable).getExceptionType() == TECHNICAL_ERROR)
                .verify();
    }

    @Test
    void shouldMapErrorOnUpdateSeat() {
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, theaterId);

        var seatRequest = SeatMockFactory.buildSeatRequestDto(roomId);
        var seatId = UUID.randomUUID();
        var seatEntity = SeatMockFactory.buildSeatEntity(seatId, roomId);

        when(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity));
        when(seatRepository.findById(seatId)).thenReturn(Mono.just(seatEntity));
        when(seatRepository.save(any())).thenReturn(Mono.error(new RuntimeException("DB Update Failure")));

        var response = seatService.updateSeat(theaterId, seatId, seatRequest);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof CinemaException &&
                        throwable.getMessage().equals("DB error updating seat") &&
                        ((CinemaException) throwable).getExceptionType() == TECHNICAL_ERROR)
                .verify();
    }

    @Test
    void shouldMapErrorOnDeleteSeat() {
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, theaterId);

        var seatId = UUID.randomUUID();
        var seatEntity = SeatMockFactory.buildSeatEntity(seatId, roomId);

        when(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity));
        when(seatRepository.findById(seatId)).thenReturn(Mono.just(seatEntity));
        when(seatRepository.deleteById(seatId)).thenReturn(Mono.error(new RuntimeException("DB Delete Failure")));

        var response = seatService.deleteSeat(theaterId, seatId);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof CinemaException &&
                        throwable.getMessage().equals("DB error deleting seat") &&
                        ((CinemaException) throwable).getExceptionType() == TECHNICAL_ERROR)
                .verify();
    }
}