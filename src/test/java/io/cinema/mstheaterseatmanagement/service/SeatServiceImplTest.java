package io.cinema.mstheaterseatmanagement.service;

import io.cinema.mstheaterseatmanagement.domain.dto.response.SeatResponseDto;
import io.cinema.mstheaterseatmanagement.factory.RoomMockFactory;
import io.cinema.mstheaterseatmanagement.factory.SeatMockFactory;
import io.cinema.mstheaterseatmanagement.mapper.SeatMapper;
import io.cinema.mstheaterseatmanagement.mapper.SeatMapperImpl;
import io.cinema.mstheaterseatmanagement.repository.RoomRepository;
import io.cinema.mstheaterseatmanagement.repository.SeatRepository;
import io.cinema.mstheaterseatmanagement.service.impl.SeatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@Import({SeatMapper.class})
class SeatServiceImplTest {
    @Autowired
    private SeatMapperImpl seatMapper;

    private SeatRepository seatRepository;
    private RoomRepository roomRepository;
    private TransactionalOperator transactionalOperator;
    private SeatService seatService;

    @BeforeEach
    void setUp() {
        seatRepository = Mockito.mock(SeatRepository.class);
        roomRepository = Mockito.mock(RoomRepository.class);
        transactionalOperator = Mockito.mock(TransactionalOperator.class);
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
        // given
        var roomId = UUID.randomUUID();
        var theaterId = UUID.randomUUID();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, theaterId);

        var seatId = UUID.randomUUID();
        var seats = SeatMockFactory.buildSeatEntity(seatId, roomId);
        var seatResponse = SeatMockFactory.buildSeatResponseDto(seatId, roomId);

        // when
        when(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity));
        when(seatRepository.findAllByRoomId(roomId)).thenReturn(Flux.just(seats));

        // then
        var response = seatService.getAllSeats(theaterId, roomId);

        StepVerifier.create(response)
                .expectNext(seatResponse)
                .verifyComplete();

        verify(roomRepository).findById(roomId);
        verify(seatRepository).findAllByRoomId(roomId);
    }

    @Test
    void shouldCreateSeats(){
        // given
        var theaterId = UUID.randomUUID();
        var roomId = UUID.randomUUID();
        var roomEntity = RoomMockFactory.buildRoomEntity(roomId, theaterId);

        var seatRequest = SeatMockFactory.buildSeatRequestDto(roomId);
        var seatRequests = List.of(seatRequest);
        var seatId = UUID.randomUUID();
        var seatEntity = SeatMockFactory.buildSeatEntity(seatId, roomId);

        var seatResponse =SeatMockFactory.buildSeatRequestDto(roomId);

        // when
        when(roomRepository.findById(roomId)).thenReturn(Mono.just(roomEntity));
        when(seatRepository.saveAll(anyIterable())).thenReturn(Flux.just(seatEntity));

        // then
        Flux<SeatResponseDto> response = seatService.createSeats(theaterId, seatRequests);

        StepVerifier.create(response)
                .expectNext(seatResponse)
                .verifyComplete();

        verify(roomRepository).findById(roomId);
        verify(seatRepository).saveAll(anyIterable());
        verify(transactionalOperator).transactional(any(Flux.class));

    }
}