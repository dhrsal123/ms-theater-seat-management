package io.cinema.mstheaterseatmanagement.mapper;

import io.cinema.mstheaterseatmanagement.domain.dto.request.TheaterRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.OperatingHoursResponseDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.TheaterResponseDto;
import io.cinema.mstheaterseatmanagement.domain.entity.AddressEntity;
import io.cinema.mstheaterseatmanagement.domain.entity.OperatingHoursEntity;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterEntity;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterRowProjection;
import io.cinema.mstheaterseatmanagement.utils.AddressUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.UUID;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        imports = {AddressUtils.class},
        uses = {OperatingHoursMapper.class}
)
public interface TheaterMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addressId", source = "addressId")
    TheaterEntity toEntity(TheaterRequestDto dto, UUID addressId);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addressId", ignore = true)
    void updateEntityFromDto(TheaterRequestDto dto, @MappingTarget TheaterEntity entity);

    @Mapping(target = "theaterId", expression = "java(entity.getId().toString())")
    @Mapping(target = "location", expression = "java(AddressUtils.getAddress(address))")
    TheaterResponseDto toResponseDto(
            TheaterEntity entity,
            AddressEntity address,
            List<OperatingHoursEntity> operatingHours
    );

    default TheaterResponseDto toTheaterDtoFromProjections(List<TheaterRowProjection> rows, UUID theaterId) {
        if (rows == null || rows.isEmpty()) return null;
        var first = rows.getFirst();

        var ohList = rows.stream()
                .filter(r -> r.dayOfWeek() != null)
                .map(r -> new OperatingHoursResponseDto(r.dayOfWeek(), r.startTime(), r.endTime()))
                .toList();

        var address = AddressUtils.toAddress(first.street(), first.city(), first.state(), first.country(), first.zip());

        return TheaterResponseDto.builder()
                .theaterId(theaterId.toString())
                .name(first.name())
                .email(first.email())
                .phone(first.phone())
                .location(address)
                .operatingHours(ohList)
                .build();
    }
}