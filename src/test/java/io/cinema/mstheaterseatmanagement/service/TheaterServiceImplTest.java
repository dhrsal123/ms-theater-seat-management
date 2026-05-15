package io.cinema.mstheaterseatmanagement.service;

import io.cinema.domain.exceptions.CinemaException;
import io.cinema.mstheaterseatmanagement.domain.dto.request.TheaterRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.TheaterResponseDto;
import io.cinema.mstheaterseatmanagement.domain.entity.AddressEntity;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterEntity;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterRowProjection;
import io.cinema.mstheaterseatmanagement.factory.AddressMockFactory;
import io.cinema.mstheaterseatmanagement.factory.OperatingHoursMockFactory;
import io.cinema.mstheaterseatmanagement.factory.TheaterMockFactory;
import io.cinema.mstheaterseatmanagement.mapper.AddressMapper;
import io.cinema.mstheaterseatmanagement.mapper.AddressMapperImpl;
import io.cinema.mstheaterseatmanagement.mapper.OperatingHoursMapper;
import io.cinema.mstheaterseatmanagement.mapper.OperatingHoursMapperImpl;
import io.cinema.mstheaterseatmanagement.mapper.TheaterMapper;
import io.cinema.mstheaterseatmanagement.mapper.TheaterMapperImpl;
import io.cinema.mstheaterseatmanagement.repository.AddressRepository;
import io.cinema.mstheaterseatmanagement.repository.OperatingHoursRepository;
import io.cinema.mstheaterseatmanagement.repository.RoomRepository;
import io.cinema.mstheaterseatmanagement.repository.SeatRepository;
import io.cinema.mstheaterseatmanagement.repository.TheaterRepository;
import io.cinema.mstheaterseatmanagement.service.impl.TheaterServiceImpl;
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

import java.util.UUID;

import static io.cinema.domain.enumerated.CinemaExceptionTypes.NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@Import({TheaterMapperImpl.class, AddressMapperImpl.class, OperatingHoursMapperImpl.class})
class TheaterServiceImplTest {

    private TheaterRepository theaterRepository;
    private AddressRepository addressRepository;
    private OperatingHoursRepository operatingHoursRepository;
    private RoomRepository roomRepository;
    private SeatRepository seatRepository;
    private TransactionalOperator transactionalOperator;


    @Autowired
    private TheaterMapper theaterMapper;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private OperatingHoursMapper operatingHoursMapper;


    private TheaterServiceImpl theaterService;

    @BeforeEach
    void setUp() {
        theaterRepository = Mockito.mock(TheaterRepository.class);
        addressRepository = Mockito.mock(AddressRepository.class);
        operatingHoursRepository = Mockito.mock(OperatingHoursRepository.class);
        roomRepository = Mockito.mock(RoomRepository.class);
        seatRepository = Mockito.mock(SeatRepository.class);
        transactionalOperator = Mockito.mock(TransactionalOperator.class);

        theaterService = new TheaterServiceImpl(
                theaterRepository,
                addressRepository,
                operatingHoursRepository,
                roomRepository,
                seatRepository,
                transactionalOperator,
                theaterMapper,
                addressMapper,
                operatingHoursMapper
        );

        when(transactionalOperator.transactional(any(Flux.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldGetAllTheaters() {
        UUID theaterId = UUID.randomUUID();
        TheaterRowProjection projection = TheaterMockFactory.buildTheaterRowProjection(theaterId);
        TheaterResponseDto responseDto = TheaterMockFactory.buildTheaterResponseDto(theaterId);

        when(theaterRepository.findAllTheaterDetails(10, 0L)).thenReturn(Flux.just(projection));

        var response = theaterService.getAllTheaters(0, 10);

        StepVerifier.create(response)
                .expectNext(responseDto)
                .verifyComplete();

        verify(theaterRepository).findAllTheaterDetails(10, 0L);
    }

    @Test
    void shouldGetTheaterById() {
        UUID theaterId = UUID.randomUUID();
        TheaterRowProjection projection = TheaterMockFactory.buildTheaterRowProjection(theaterId);
        TheaterResponseDto responseDto = TheaterMockFactory.buildTheaterResponseDto(theaterId);

        when(theaterRepository.findTheaterDetailsById(theaterId)).thenReturn(Flux.just(projection));

        var response = theaterService.getTheaterById(theaterId);

        StepVerifier.create(response)
                .expectNext(responseDto)
                .verifyComplete();

        verify(theaterRepository).findTheaterDetailsById(theaterId);
    }

    @Test
    void shouldFailToGetTheaterByIdWhenNotFound() {
        UUID theaterId = UUID.randomUUID();

        when(theaterRepository.findTheaterDetailsById(theaterId)).thenReturn(Flux.empty());

        var response = theaterService.getTheaterById(theaterId);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof CinemaException &&
                        throwable.getMessage().equals("Theater not found") &&
                        ((CinemaException) throwable).getExceptionType() == NOT_FOUND)
                .verify();
    }

    @Test
    void shouldCreateTheater() {
        UUID theaterId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        TheaterRequestDto requestDto = TheaterMockFactory.buildTheaterRequestDto();
        AddressEntity addressEntity = AddressMockFactory.buildAddressEntity(addressId);
        TheaterEntity theaterEntity = TheaterMockFactory.buildTheaterEntity(theaterId, addressId);
        TheaterResponseDto responseDto = TheaterMockFactory.buildTheaterResponseDto(theaterId);
        var operatingHourId = UUID.randomUUID();
        var operatingHour = OperatingHoursMockFactory.buildOperatingHoursEntity(operatingHourId, theaterId);

        when(addressRepository.save(any(AddressEntity.class))).thenReturn(Mono.just(addressEntity));
        when(theaterRepository.save(any(TheaterEntity.class))).thenReturn(Mono.just(theaterEntity));
        when(operatingHoursRepository.saveAll(anyIterable())).thenReturn(Flux.just(operatingHour));

        var response = theaterService.createTheater(requestDto);

        StepVerifier.create(response)
                .expectNext(responseDto)
                .verifyComplete();
    }

    @Test
    void shouldUpdateTheater() {
        UUID theaterId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        TheaterRequestDto requestDto = TheaterMockFactory.buildTheaterRequestDto();

        TheaterEntity theaterEntity = TheaterMockFactory.buildTheaterEntity(theaterId, addressId);

        AddressEntity addressEntity = AddressMockFactory.buildAddressEntity(addressId);

        TheaterResponseDto responseDto = TheaterMockFactory.buildTheaterResponseDto(theaterId);

        var operatingHourId = UUID.randomUUID();
        var operatingHour = OperatingHoursMockFactory.buildOperatingHoursEntity(operatingHourId, theaterId);

        when(theaterRepository.findById(theaterId)).thenReturn(Mono.just(theaterEntity));
        when(addressRepository.findById(addressId)).thenReturn(Mono.just(addressEntity));
        when(addressRepository.save(any(AddressEntity.class))).thenReturn(Mono.just(addressEntity));
        when(theaterRepository.save(any(TheaterEntity.class))).thenReturn(Mono.just(theaterEntity));
        when(operatingHoursRepository.deleteAllByTheaterId(theaterId)).thenReturn(Mono.empty());
        when(operatingHoursRepository.saveAll(anyIterable())).thenReturn(Flux.just(operatingHour));

        var response = theaterService.updateTheater(theaterId, requestDto);

        StepVerifier.create(response)
                .expectNext(responseDto)
                .verifyComplete();

        verify(theaterRepository).save(any(TheaterEntity.class));
        verify(addressRepository).save(any(AddressEntity.class));
    }

    @Test
    void shouldFailToUpdateTheaterWhenNotFound() {
        UUID theaterId = UUID.randomUUID();
        TheaterRequestDto requestDto = TheaterMockFactory.buildTheaterRequestDto();

        when(theaterRepository.findById(theaterId)).thenReturn(Mono.empty());

        var response = theaterService.updateTheater(theaterId, requestDto);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof CinemaException &&
                        throwable.getMessage().equals("Theater not found"))
                .verify();
    }

    @Test
    void shouldDeleteTheaterAndDependencies() {
        UUID theaterId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        TheaterEntity theaterEntity = TheaterMockFactory.buildTheaterEntity(theaterId, addressId);

        when(theaterRepository.findById(theaterId)).thenReturn(Mono.just(theaterEntity));

        when(roomRepository.findRoomEntityByTheaterId(theaterId)).thenReturn(Flux.empty());

        when(operatingHoursRepository.deleteAllByTheaterId(theaterId)).thenReturn(Mono.empty());
        when(theaterRepository.delete(theaterEntity)).thenReturn(Mono.empty());
        when(addressRepository.deleteById(addressId)).thenReturn(Mono.empty());

        var response = theaterService.deleteTheater(theaterId);

        StepVerifier.create(response)
                .verifyComplete();

        verify(theaterRepository).delete(theaterEntity);
        verify(addressRepository).deleteById(addressId);
        verify(operatingHoursRepository).deleteAllByTheaterId(theaterId);
    }

    @Test
    void shouldFailToDeleteTheaterWhenNotFound() {
        UUID theaterId = UUID.randomUUID();

        when(theaterRepository.findById(theaterId)).thenReturn(Mono.empty());

        var response = theaterService.deleteTheater(theaterId);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof CinemaException &&
                        throwable.getMessage().equals("Theater not found"))
                .verify();
    }
}