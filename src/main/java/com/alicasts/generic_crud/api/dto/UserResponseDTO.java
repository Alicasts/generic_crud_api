package com.alicasts.generic_crud.api.dto;

import com.alicasts.generic_crud.model.Sex;

import java.time.Instant;

public record UserResponseDTO(Long id, String name, String email, Integer age, String cpf, String cep,
                              String address, Sex sex, Instant createdAt, Instant updatedAt) {

}
