package com.alicasts.generic_crud.api;

import com.alicasts.generic_crud.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerValidationTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    IUserService userService;

    @Test
    void create_invalidPayload_returns400() throws Exception {
        String invalid = """
        {
          "name": "",
          "email": "not-an-email",
          "age": 131,
          "cpf": "123",
          "cep": "abc",
          "address": "",
          "sex": "NA"
        }
        """;

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void create_invalidEmail_returns400_andServiceNotCalled() throws Exception {
        String invalidEmailOnly = """
        {
          "name": "Ana Silva",
          "email": "not-an-email",
          "age": 28,
          "cpf": "12345678901",
          "cep": "88000000",
          "address": "Rua X, 100",
          "sex": "FEMALE"
        }
        """;

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEmailOnly))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].field").value("email"));

        verifyNoInteractions(userService);
    }

    @Test
    void create_invalidCpf_returns400_andServiceNotCalled() throws Exception {
        String invalidCpfOnly = """
        {
          "name": "Ana Silva",
          "email": "ana@example.com",
          "age": 28,
          "cpf": "123",
          "cep": "88000000",
          "address": "Rua X, 100",
          "sex": "FEMALE"
        }
        """;

        mvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidCpfOnly))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].field").value("cpf"));

        verifyNoInteractions(userService);
    }
}
