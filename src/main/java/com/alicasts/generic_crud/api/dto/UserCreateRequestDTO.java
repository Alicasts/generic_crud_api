package com.alicasts.generic_crud.api.dto;

import com.alicasts.generic_crud.model.Sex;
import jakarta.validation.constraints.*;

public class UserCreateRequestDTO {

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotBlank
    @Email
    @Size(max = 254)
    private String email;

    @NotNull
    @Min(0) @Max(130)
    private Integer age;

    @NotBlank
    @Pattern(regexp = "\\d{11}")
    private String cpf;

    @NotBlank
    @Pattern(regexp = "\\d{8}")
    private String cep;

    @NotBlank
    @Size(max = 200)
    private String address;

    @NotNull
    private Sex sex;

    public void UserCreateRequest() {}

    public String getName() { return name; }
    public String getEmail() { return email; }
    public Integer getAge() { return age; }
    public String getCpf() { return cpf; }
    public String getCep() { return cep; }
    public String getAddress() { return address; }
    public Sex getSex() { return sex; }
}
