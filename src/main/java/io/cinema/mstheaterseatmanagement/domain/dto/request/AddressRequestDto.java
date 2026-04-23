package io.cinema.mstheaterseatmanagement.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequestDto(
        @NotBlank(message = "Street cannot be empty")
        @Size(max = 255, message = "Street cannot exceed 255 characters")
        String street,

        @NotBlank(message = "City cannot be empty")
        @Size(max = 100, message = "City cannot exceed 100 characters")
        String city,

        @NotBlank(message = "State cannot be empty")
        @Size(max = 100, message = "State cannot exceed 100 characters")
        String state,

        @NotBlank(message = "Country cannot be empty")
        @Size(max = 100, message = "Country cannot exceed 100 characters")
        String country,

        @NotBlank(message = "ZIP code cannot be empty")
        @Size(max = 20, message = "ZIP code cannot exceed 20 characters")
        String zip
) {
}
