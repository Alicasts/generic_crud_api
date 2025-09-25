package com.alicasts.generic_crud.api.dto;

import com.alicasts.generic_crud.model.Sex;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserResponseDTOTest {

    @Test
    void getters_returnConstructorValues() {
        Instant now = Instant.parse("2025-09-16T13:00:00Z");

        var dto = new UserResponseDTO(
                1L,
                "Ana Silva",
                "ana@example.com",
                28,
                "12345678901",
                "88000000",
                "Rua X, 100",
                Sex.FEMALE,
                now,
                now
        );

        assertEquals(1L, dto.id());
        assertEquals("Ana Silva", dto.name());
        assertEquals("ana@example.com", dto.email());
        assertEquals(28, dto.age());
        assertEquals("12345678901", dto.cpf());
        assertEquals("88000000", dto.cep());
        assertEquals("Rua X, 100", dto.address());
        assertEquals(Sex.FEMALE, dto.sex());
        assertEquals(now, dto.createdAt());
        assertEquals(now, dto.updatedAt());
    }
}
