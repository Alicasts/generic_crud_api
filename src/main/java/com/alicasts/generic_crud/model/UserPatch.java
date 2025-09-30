package com.alicasts.generic_crud.model;

public record UserPatch(
        String name,
        Integer age,
        String cpf,
        String cep,
        String address,
        Sex sex
) {}
