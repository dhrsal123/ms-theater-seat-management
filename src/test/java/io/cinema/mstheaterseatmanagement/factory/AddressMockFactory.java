package io.cinema.mstheaterseatmanagement.factory;

import io.cinema.mstheaterseatmanagement.domain.dto.request.AddressRequestDto;
import io.cinema.mstheaterseatmanagement.domain.entity.AddressEntity;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class AddressMockFactory {

    public static AddressEntity buildAddressEntity(UUID addressId) {
        return new AddressEntity(
                addressId,
                "St 123 Av Heaven",
                "New York",
                "Tolima",
                "China",
                "123456"
        );
    }

    public static AddressRequestDto buildAddressRequestDto() {
        return new AddressRequestDto(
                "St 123 Av Heaven",
                "New York",
                "Tolima",
                "China",
                "123456"
        );
    }
}
