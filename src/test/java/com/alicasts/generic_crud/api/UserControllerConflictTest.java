package com.alicasts.generic_crud.api;

import com.alicasts.generic_crud.api.dto.UserCreateRequestDTO;
import com.alicasts.generic_crud.service.IUserService;
import com.alicasts.generic_crud.service.exception.ResourceConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerConflictTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    IUserService userService;

    @Test
    void create_duplicateEmailAndCpf_returns409_withErrorsList() throws Exception {
        when(userService.create(any(UserCreateRequestDTO.class)))
                .thenThrow(new ResourceConflictException(List.of("email", "cpf")));

        String body = """
        {
          "name": "Ana Silva",
          "email": "ana@example.com",
          "age": 28,
          "cpf": "12345678901",
          "cep": "88000000",
          "address": "Rua X, 100",
          "sex": "FEMALE"
        }
        """;

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RESOURCE_CONFLICT"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].message").value("already exists"))
                .andExpect(jsonPath("$.errors[1].field").value("cpf"))
                .andExpect(jsonPath("$.errors[1].message").value("already exists"));
    }

    @Test
    void create_duplicateEmail_returns409_withEmailError() throws Exception {
        when(userService.create(any(UserCreateRequestDTO.class)))
                .thenThrow(new ResourceConflictException(List.of("email")));

        String body = """
    {
      "name": "Ana Silva",
      "email": "ana@example.com",
      "age": 28,
      "cpf": "12345678901",
      "cep": "88000000",
      "address": "Rua X, 100",
      "sex": "FEMALE"
    }
    """;

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RESOURCE_CONFLICT"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field").value("email"))
                .andExpect(jsonPath("$.errors[0].message").value("already exists"));
    }

    @Test
    void create_duplicateCpf_returns409_withCpfError() throws Exception {
        when(userService.create(any(UserCreateRequestDTO.class)))
                .thenThrow(new ResourceConflictException(List.of("cpf")));

        String body = """
    {
      "name": "Ana Silva",
      "email": "ana@example.com",
      "age": 28,
      "cpf": "12345678901",
      "cep": "88000000",
      "address": "Rua X, 100",
      "sex": "FEMALE"
    }
    """;

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RESOURCE_CONFLICT"))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field").value("cpf"))
                .andExpect(jsonPath("$.errors[0].message").value("already exists"));
    }
}
