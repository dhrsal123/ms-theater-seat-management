package io.cinema.mstheaterseatmanagement.mapper;

import io.cinema.mstheaterseatmanagement.domain.dto.request.SeatRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.SeatResponseDto;
import io.cinema.mstheaterseatmanagement.domain.entity.SeatEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SeatMapper {

    @Mapping(target = "id", ignore = true)
    SeatEntity toEntity(SeatRequestDto seatRequestDto);

    @Mapping(source = "id", target = "seatId")
    SeatResponseDto toResponseDto(SeatEntity seatEntity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(SeatRequestDto seatRequestDto, @MappingTarget SeatEntity seatEntity);
}