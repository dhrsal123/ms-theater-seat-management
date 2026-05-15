package io.cinema.mstheaterseatmanagement.service;

import io.cinema.domain.exceptions.CinemaException;
import io.cinema.mstheaterseatmanagement.domain.entity.RoomEntity;
import io.cinema.mstheaterseatmanagement.factory.RoomMockFactory;
import io.cinema.mstheaterseatmanagement.factory.TheaterMockFactory;
import io.cinema.mstheaterseatmanagement.mapper.RoomMapperImpl;
import io.cinema.mstheaterseatmanagement.repository.RoomRepository;
import io.cinema.mstheaterseatmanagement.repository.SeatRepository;
import io.cinema.mstheaterseatmanagement.repository.TheaterRepository;
import io.cinema.mstheaterseatmanagement.service.impl.RoomServiceImpl;
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

import static io.cinema.domain.enumerated.CinemaExceptionTypes.NOT_FOUND;
import static io.cinema.domain.enumerated.CinemaExceptionTypes.TECHNICAL_ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import({RoomMapperImpl.class})
class RoomServiceImplTest {
    private RoomRepository roomRepository;
    private TheaterRepository theaterRepository;
    private TransactionalOperator transactionalOperator;
    private SeatRepository seatRepository;
    private RoomMapperImpl roomMapper;
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        roomRepository = Mockito.mock(RoomRepository.class);
        theaterRepository = Mockito.mock(TheaterRepository.class);
        transactionalOperator = Mockito.mock(TransactionalOperator.class);
        seatRepository = Mockito.mock(SeatRepository.class);
        roomMapper = new RoomMapperImpl();
        roomService = new RoomServiceImpl(
                roomRepository,
                theaterRepository,
                transactionalOperator,
                seatRepository,
                roomMapper
        );

        when(transactionalOperator.transactional(any(Flux.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldGetAllRooms() {
        // given
        var roomId = UUID.randomUUID();
        var theaterId = UUID.randomUUID();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, theaterId);
        var roomResponse = RoomMockFactory.buildRoomResponseDto(roomId, theaterId);

        // when
        when(roomRepository.findRoomEntityByTheaterId(theaterId)).thenReturn(Flux.just(roomEntity));

        // then
        var response = roomService.getAllRooms(theaterId);

        StepVerifier.create(response)
                .expectNext(roomResponse)
                .verifyComplete();

        verify(roomRepository).findRoomEntityByTheaterId(theaterId);
        verify(transactionalOperator).transactional(any(Flux.class));
    }

    @Test
    void shouldSaveRooms() {
        // given
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var addressId = UUID.randomUUID();
        var theater = TheaterMockFactory.buildTheaterEntity(theaterId, addressId);

        var roomRequest = RoomMockFactory.buildRoomRequestDto();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, theaterId);
        var roomResponse = RoomMockFactory.buildRoomResponseDto(roomId, theaterId);

        // when
        when(theaterRepository.findById(theaterId)).thenReturn(Mono.just(theater));
        when(roomRepository.saveAll(anyIterable())).thenReturn(Flux.just(roomEntity));

        // then
        var response = roomService.saveRooms(theaterId, List.of(roomRequest));

        StepVerifier.create(response)
                .expectNext(roomResponse)
                .verifyComplete();

        verify(theaterRepository).findById(theaterId);
        verify(roomRepository).saveAll(anyIterable());
        verify(transactionalOperator).transactional(any(Flux.class));
    }

    @Test
    void shouldFailToSaveRoomsWhenTheaterNotFound() {
        // given
        var theaterId = UUID.randomUUID();
        var roomRequest = RoomMockFactory.buildRoomRequestDto();

        // when
        when(theaterRepository.findById(theaterId)).thenReturn(Mono.empty());

        // then
        var response = roomService.saveRooms(theaterId, List.of(roomRequest));

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof CinemaException &&
                        throwable.getMessage().equals("Theater not found.") &&
                        ((CinemaException) throwable).getExceptionType() == NOT_FOUND)
                .verify();

        verify(theaterRepository).findById(theaterId);
    }

    @Test
    void shouldUpdateRoom() {
        // given
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var roomRequest = RoomMockFactory.buildRoomRequestDto();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, theaterId);
        var roomResponse = RoomMockFactory.buildRoomResponseDto(roomId, theaterId);

        // when
        when(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity));
        when(roomRepository.save(any(RoomEntity.class))).thenReturn(Mono.just(roomEntity));

        // then
        var response = roomService.updateRoom(theaterId, roomId, roomRequest);

        StepVerifier.create(response)
                .expectNext(roomResponse)
                .verifyComplete();

        verify(roomRepository).findById(roomId);
        verify(roomRepository).save(any(RoomEntity.class));
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldFailToUpdateRoomWhenBelongsToDifferentTheater() {
        // given
        var expectedTheaterId = UUID.randomUUID();
        var actualTheaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var roomRequest = RoomMockFactory.buildRoomRequestDto();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, actualTheaterId);

        // when
        when(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity));

        // then
        var response = roomService.updateRoom(expectedTheaterId, roomId, roomRequest);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof CinemaException &&
                        throwable.getMessage().equals("Room does not belong to the specified theater"))
                .verify();

        verify(roomRepository).findById(roomId);
    }

    @Test
    void shouldDeleteRoom() {
        // given
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, theaterId);

        // when
        when(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity));
        when(seatRepository.deleteAllByRoomId(roomId)).thenReturn(Mono.empty());
        when(roomRepository.deleteById(roomId)).thenReturn(Mono.empty());

        // then
        var response = roomService.deleteRoom(theaterId, roomId);

        StepVerifier.create(response)
                .verifyComplete();

        verify(roomRepository).findById(roomId);
        verify(seatRepository).deleteAllByRoomId(roomId);
        verify(roomRepository).deleteById(roomId);
        verify(transactionalOperator).transactional(any(Mono.class));
    }

    @Test
    void shouldMapErrorOnGetAllRooms() {
        // given
        var theaterId = UUID.randomUUID();

        // when
        when(roomRepository.findRoomEntityByTheaterId(theaterId))
                .thenReturn(Flux.error(new RuntimeException("Connection timeout")));

        // then
        var response = roomService.getAllRooms(theaterId);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof CinemaException &&
                        throwable.getMessage().equals("DB error during read") &&
                        ((CinemaException) throwable).getExceptionType() == TECHNICAL_ERROR)
                .verify();

        verify(roomRepository).findRoomEntityByTheaterId(theaterId);
    }

    @Test
    void shouldMapErrorOnSaveRooms() {
        // given
        var theaterId = UUID.randomUUID();
        var addressId = UUID.randomUUID();
        var theater = TheaterMockFactory.buildTheaterEntity(theaterId, addressId);
        var roomRequest = RoomMockFactory.buildRoomRequestDto();

        // when
        when(theaterRepository.findById(theaterId)).thenReturn(Mono.just(theater));
        when(roomRepository.saveAll(anyIterable()))
                .thenReturn(Flux.error(new RuntimeException("Integrity constraint violation")));

        // then
        var response = roomService.saveRooms(theaterId, List.of(roomRequest));

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof CinemaException &&
                        throwable.getMessage().equals("DB error during save") &&
                        ((CinemaException) throwable).getExceptionType() == TECHNICAL_ERROR)
                .verify();

        verify(theaterRepository).findById(theaterId);
        verify(roomRepository).saveAll(anyIterable());
    }

    @Test
    void shouldMapErrorOnUpdateRoom() {
        // given
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var roomRequest = RoomMockFactory.buildRoomRequestDto();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, theaterId);

        // when
        when(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity));
        when(roomRepository.save(any(RoomEntity.class)))
                .thenReturn(Mono.error(new RuntimeException("Database down")));

        // then
        var response = roomService.updateRoom(theaterId, roomId, roomRequest);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof CinemaException &&
                        throwable.getMessage().equals("DB error during update") &&
                        ((CinemaException) throwable).getExceptionType() == TECHNICAL_ERROR)
                .verify();

        verify(roomRepository).findById(roomId);
        verify(roomRepository).save(any(RoomEntity.class));
    }

    @Test
    void shouldMapErrorOnDeleteRoom() {
        // given
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, theaterId);

        // when
        when(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity));
        when(seatRepository.deleteAllByRoomId(roomId))
                .thenReturn(Mono.error(new RuntimeException("Deadlock found")));

        // then
        var response = roomService.deleteRoom(theaterId, roomId);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof CinemaException &&
                        throwable.getMessage().equals("DB error during deletion") &&
                        ((CinemaException) throwable).getExceptionType() == TECHNICAL_ERROR)
                .verify();

        verify(roomRepository).findById(roomId);
        verify(seatRepository).deleteAllByRoomId(roomId);
    }
}