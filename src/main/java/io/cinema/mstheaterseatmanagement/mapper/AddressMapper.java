package io.cinema.mstheaterseatmanagement.mapper;

import io.cinema.mstheaterseatmanagement.domain.dto.request.AddressRequestDto;
import io.cinema.mstheaterseatmanagement.domain.entity.AddressEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    AddressEntity toEntity(AddressRequestDto dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(AddressRequestDto dto, @MappingTarget AddressEntity entity);
}