package io.cinema.mstheaterseatmanagement.utils;

import io.cinema.mstheaterseatmanagement.domain.entity.AddressEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AddressUtilsTest {

    private static Stream<Arguments> provideAddressScenarios() {
        return Stream.of(
                Arguments.of("All fields present", "Street", "City", "State", "Country", "12345", "Street, City, State, Country, ZIP Code: 12345"),

                Arguments.of("Missing state and country", "Street", "City", null, "", "12345", "Street, City, ZIP Code: 12345"),
                Arguments.of("Missing street and city", null, " ", "State", "Country", "12345", "State, Country, ZIP Code: 12345"),

                Arguments.of("No ZIP code provided (null)", "Street", "City", "State", "Country", null, "Street, City, State, Country"),
                Arguments.of("No ZIP code provided (blank)", "Street", "City", "State", "Country", "  ", "Street, City, State, Country"),
                Arguments.of("Only ZIP code present", null, null, null, null, "12345", "ZIP Code: 12345"),

                Arguments.of("Fields with leading/trailing spaces", " Street ", " City ", " State ", " Country ", " 12345 ", "Street, City, State, Country, ZIP Code: 12345"),

                Arguments.of("All fields null", null, null, null, null, null, ""),
                Arguments.of("All fields blank strings", "", "  ", "\t", "\n", " ", "")
        );
    }

    @Test
    void shouldReturnEmptyString_WhenEntityIsNull() {
        assertEquals("", AddressUtils.getAddress(null));
    }

    @Test
    void shouldReturnFormattedString_WhenEntityIsPopulated() {
        // arrange
        AddressEntity address = mock(AddressEntity.class);
        when(address.getStreet()).thenReturn("123 Cinema Lane");
        when(address.getCity()).thenReturn("Hollywood");
        when(address.getState()).thenReturn("CA");
        when(address.getCountry()).thenReturn("USA");
        when(address.getZip()).thenReturn("90210");

        // act
        String result = AddressUtils.getAddress(address);

        // assert
        assertEquals("123 Cinema Lane, Hollywood, CA, USA, ZIP Code: 90210", result);
    }

    @ParameterizedTest(name = "Test {index}: {0}")
    @MethodSource("provideAddressScenarios")
    void shouldHandleAllScenarios(String description, String street, String city, String state, String country, String zip, String expected) {
        String result = AddressUtils.toAddress(street, city, state, country, zip);
        assertEquals(expected, result);
    }
}