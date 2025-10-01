package com.alicasts.generic_crud.api;

import com.alicasts.generic_crud.api.dto.UserCreateRequestDTO;
import com.alicasts.generic_crud.api.dto.UserResponseDTO;
import com.alicasts.generic_crud.api.imp.UserController;
import com.alicasts.generic_crud.model.Sex;
import com.alicasts.generic_crud.service.IUserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerCreatedTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    IUserService userService;

    @Test
    void create_shouldReturn201_andLocation_andNormalizedBody() throws Exception {
        String body = """
        {
          "name": "Ana Silva",
          "email": "Ana.SILVA+test@example.com",
          "age": 28,
          "cpf": "12345678901",
          "cep": "88000000",
          "address": "Rua X, 100",
          "sex": "FEMALE"
        }
        """;

        var response = new UserResponseDTO(
                1L,
                "Ana Silva",
                "ana.silva+test@example.com",
                28,
                "12345678901",
                "88000000",
                "Rua X, 100",
                Sex.FEMALE,
                Instant.parse("2025-09-16T13:00:00Z"),
                Instant.parse("2025-09-16T13:00:00Z")
        );
        when(userService.create(any(UserCreateRequestDTO.class))).thenReturn(response);

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", Matchers.matchesPattern(".*/api/users/1$")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("ana.silva+test@example.com"))
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.cep").value("88000000"))
                .andExpect(jsonPath("$.name").value("Ana Silva"))
                .andExpect(jsonPath("$.sex").value("FEMALE"));
    }
}
