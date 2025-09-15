package com.alicasts.generic_crud.model;

import com.alicasts.generic_crud.persistence.converter.DigitsOnlyConverter;
import com.alicasts.generic_crud.persistence.converter.EmailLowercaseConverter;
import com.alicasts.generic_crud.persistence.converter.TrimConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.Instant;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_users_cpf", columnNames = "cpf")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    @Convert(converter = TrimConverter.class)
    private String name;

    @NotBlank
    @Email
    @Size(max = 254)
    @Column(nullable = false, length = 254)
    @Convert(converter = EmailLowercaseConverter.class)
    private String email;

    @NotNull
    @Min(0) @Max(130)
    @Column(nullable = false)
    private Integer age;

    @NotBlank
    @Pattern(regexp = "\\d{11}")
    @Column(nullable = false, length = 11)
    @Convert(converter = DigitsOnlyConverter.class)
    private String cpf;

    @NotBlank
    @Pattern(regexp = "\\d{8}")
    @Column(nullable = false, length = 8)
    @Convert(converter = DigitsOnlyConverter.class)
    private String cep;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    @Convert(converter = TrimConverter.class)
    private String address;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Sex sex;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected User() {
    }

    public User(String name,
                String email,
                Integer age,
                String cpf,
                String cep,
                String address,
                Sex sex) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.cpf = cpf;
        this.cep = cep;
        this.address = address;
        this.sex = sex;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
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
