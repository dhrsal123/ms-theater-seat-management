package io.cinema.mstheaterseatmanagement.mapper;

import io.cinema.mstheaterseatmanagement.domain.dto.request.TheaterRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.request.UpdateTheaterRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.OperatingHoursResponseDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.TheaterResponseDto;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterEntity;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterRowProjection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

import static io.cinema.mstheaterseatmanagement.utils.AddressUtils.getAddress;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TheaterMapper {

    public static TheaterResponseDto toTheaterDto(List<TheaterRowProjection> projections, UUID theaterId) {
        if (projections.isEmpty()) {
            return null;
        }
        var base = projections.getFirst();
        var hoursList = projections.stream()
                .filter(r -> r.dayOfWeek() != null)
                .map(projection ->
                        new OperatingHoursResponseDto(
                                projection.dayOfWeek(),
                                projection.startTime(),
                                projection.endTime()
                        ))
                .toList();

        var address = getAddress(base);

        return new TheaterResponseDto(
                theaterId.toString(),
                base.name(),
                base.email(),
                base.phone(),
                address,
                hoursList
        );
    }

    public static TheaterEntity toTheaterEntity(UUID addressId, TheaterRequestDto theaterRequestDto) {
        return TheaterEntity.builder()
                .name(theaterRequestDto.name())
                .email(theaterRequestDto.email())
                .phone(theaterRequestDto.phone())
                .addressId(addressId)
                .build();
    }


    public static TheaterEntity toTheaterEntity(UUID addressId, UpdateTheaterRequestDto theaterRequestDto) {
        return TheaterEntity.builder()
                .id(theaterRequestDto.theaterId())
                .name(theaterRequestDto.name())
                .email(theaterRequestDto.email())
                .phone(theaterRequestDto.phone())
                .addressId(addressId)
                .build();
    }
}
