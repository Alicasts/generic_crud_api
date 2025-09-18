package com.alicasts.generic_crud.api.dto;

import com.alicasts.generic_crud.model.Sex;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserCreateResponseDTOTest {

    @Test
    void getters_returnConstructorValues() {
        Instant now = Instant.parse("2025-09-16T13:00:00Z");

        var dto = new UserCreateResponseDTO(
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

        assertEquals(1L, dto.getId());
        assertEquals("Ana Silva", dto.getName());
        assertEquals("ana@example.com", dto.getEmail());
        assertEquals(28, dto.getAge());
        assertEquals("12345678901", dto.getCpf());
        assertEquals("88000000", dto.getCep());
        assertEquals("Rua X, 100", dto.getAddress());
        assertEquals(Sex.FEMALE, dto.getSex());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }
}
