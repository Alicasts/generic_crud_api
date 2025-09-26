package com.alicasts.generic_crud.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserUpdateRequest(
        @Size(min = 2, max = 100) String name,
        @Min(0) @Max(150) Integer age,
        @Pattern(regexp = "\\d{11}") String cpf,
        @Pattern(regexp = "\\d{8}") String cep,
        @Size(min = 5, max = 200) String address,
        @Pattern(regexp = "MALE|FEMALE|OTHER") String gender
) {}
