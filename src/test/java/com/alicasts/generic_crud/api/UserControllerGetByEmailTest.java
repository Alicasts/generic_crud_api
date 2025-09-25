package com.alicasts.generic_crud.api;

import com.alicasts.generic_crud.api.dto.UserResponseDTO;
import com.alicasts.generic_crud.api.exception.GlobalExceptionHandler;
import com.alicasts.generic_crud.model.Sex;
import com.alicasts.generic_crud.service.IUserService;
import com.alicasts.generic_crud.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerGetByEmailTest {

    @Autowired MockMvc mvc;

    @MockitoBean IUserService userService;

    @Test
    void byEmail_returns200_withBody() throws Exception {
        var dto = new UserResponseDTO(
                1L, "Ana", "ana@example.com", 28,
                "12345678901", "88000000", "Rua X, 100", Sex.FEMALE,
                Instant.parse("2025-09-16T13:00:00Z"),
                Instant.parse("2025-09-16T13:00:00Z")
        );
        when(userService.findByEmail(eq("ana@example.com"))).thenReturn(dto);

        mvc.perform(get("/api/users/by-email")
                        .param("email", "ana@example.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("ana@example.com"));
    }

    @Test
    void byEmail_invalidEmail_returns400_validationError_andServiceNotCalled() throws Exception {
        mvc.perform(get("/api/users/by-email")
                        .param("email", "not-an-email")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].field").value("email"));

        verifyNoInteractions(userService);
    }

    @Test
    void byEmail_notFound_returns404_withApiError() throws Exception {
        when(userService.findByEmail(eq("missing@example.com")))
                .thenThrow(new ResourceNotFoundException("user not found"));

        mvc.perform(get("/api/users/by-email")
                        .param("email", "missing@example.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("user not found"));
    }
}
