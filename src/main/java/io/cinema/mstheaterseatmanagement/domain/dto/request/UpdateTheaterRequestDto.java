package io.cinema.mstheaterseatmanagement.domain.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record UpdateTheaterRequestDto(

        @NotNull(message = "The theater id must be valid.")
        UUID theaterId,

        @NotBlank(message = "Theater name is required")
        @Size(max = 255, message = "Name cannot exceed 255 characters")
        String name,

        @Size(min = 10, max = 13, message = "Phone number must be between 10 and 13 characters")
        @Pattern(regexp = "^\\+?\\d{10,12}$", message = "Phone number must contain only digits and an optional starting '+'")
        String phone,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 255, message = "Email cannot exceed 255 characters")
        String email,

        @Valid
        @NotNull(message = "Address is required")
        AddressRequestDto address,

        @Valid
        @NotNull(message = "Operating hours are required")
        List<OperatingHoursRequestDto> operatingHours
) {
}
