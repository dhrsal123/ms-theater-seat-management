package io.cinema.mstheaterseatmanagement.mapper;

import io.cinema.mstheaterseatmanagement.domain.dto.request.RoomRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.RoomResponseDto;
import io.cinema.mstheaterseatmanagement.domain.entity.RoomEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "theaterId", source = "theaterId")
    @Mapping(target = "name", source = "dto.name")
    RoomEntity toEntity(RoomRequestDto dto, UUID theaterId);

    RoomResponseDto toResponseDto(RoomEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "theaterId", ignore = true)
    @Mapping(target = "name", source = "dto.name")
    void updateEntityFromDto(RoomRequestDto dto, @MappingTarget RoomEntity entity);
}