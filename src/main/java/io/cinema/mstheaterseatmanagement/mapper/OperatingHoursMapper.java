package io.cinema.mstheaterseatmanagement.mapper;

import io.cinema.mstheaterseatmanagement.domain.dto.request.OperatingHoursRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.OperatingHoursInfoResponseDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.OperatingHoursResponseDto;
import io.cinema.mstheaterseatmanagement.domain.entity.OperatingHoursEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OperatingHoursMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "theaterId", source = "theaterId")
    @Mapping(target = "dayOfWeek", source = "dto.dayOfWeek")
    @Mapping(target = "startTime", source = "dto.start")
    @Mapping(target = "endTime", source = "dto.end")
    OperatingHoursEntity toEntity(OperatingHoursRequestDto dto, UUID theaterId);

    @Mapping(target = "operatingHoursId", source = "id")
    @Mapping(target = "dayOfWeek", source = "dayOfWeek")
    @Mapping(target = "start", source = "startTime")
    @Mapping(target = "end", source = "endTime")
    OperatingHoursInfoResponseDto toInfoResponseDto(OperatingHoursEntity entity);

    @Mapping(target = "dayOfWeek", source = "dayOfWeek")
    @Mapping(target = "start", source = "startTime")
    @Mapping(target = "end", source = "endTime")
    OperatingHoursResponseDto toResponseDto(OperatingHoursEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "theaterId", ignore = true)
    @Mapping(target = "dayOfWeek", source = "dto.dayOfWeek")
    @Mapping(target = "startTime", source = "dto.start")
    @Mapping(target = "endTime", source = "dto.end")
    void updateEntityFromDto(OperatingHoursRequestDto dto, @MappingTarget OperatingHoursEntity entity);
}