package com.alicasts.generic_crud.api.dto;

import com.alicasts.generic_crud.model.Sex;

import java.time.Instant;

public class UserCreateResponseDTO {

    private final Long id;
    private final String name;
    private final String email;
    private final Integer age;
    private final String cpf;
    private final String cep;
    private final String address;
    private final Sex sex;
    private final Instant createdAt;
    private final Instant updatedAt;

    public UserCreateResponseDTO(Long id, String name, String email, Integer age, String cpf, String cep,
                                 String address, Sex sex, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.cpf = cpf;
        this.cep = cep;
        this.address = address;
        this.sex = sex;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Integer getAge() { return age; }
    public String getCpf() { return cpf; }
    public String getCep() { return cep; }
    public String getAddress() { return address; }
    public Sex getSex() { return sex; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

}
