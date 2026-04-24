package io.cinema.mstheaterseatmanagement.utils;

import io.cinema.mstheaterseatmanagement.domain.entity.AddressEntity;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterRowProjection;
import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class AddressUtils {

    public static String getAddress(AddressEntity address) {
        if (address == null) {
            return "";
        }
        return toAddress(
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getZip()
        );
    }

    public static String getAddress(TheaterRowProjection projection) {
        if (projection == null) {
            return "";
        }
        return toAddress(
                projection.street(),
                projection.city(),
                projection.state(),
                projection.country(),
                projection.zip()
        );
    }

    private static String toAddress(String street, String city, String state, String country, String zip) {
        String baseAddress = Stream.of(street, city, state, country)
                .filter(Objects::nonNull)
                .filter(Predicate.not(String::isBlank))
                .map(String::trim)
                .collect(Collectors.joining(", "));

        boolean hasZip = zip != null && !zip.isBlank();

        if (baseAddress.isEmpty()) {
            return hasZip ? "ZIP Code: " + zip.trim() : "";
        }

        return hasZip ? baseAddress + ", ZIP Code: " + zip.trim() : baseAddress;
    }
}
