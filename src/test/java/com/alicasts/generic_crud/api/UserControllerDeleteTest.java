package com.alicasts.generic_crud.api;

import com.alicasts.generic_crud.api.exception.GlobalExceptionHandler;
import com.alicasts.generic_crud.service.IUserService;
import com.alicasts.generic_crud.service.exception.ResourceConflictException;
import com.alicasts.generic_crud.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerDeleteTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserService userService;

    @Test
    @DisplayName("DELETE /api/users/{id} → 204 when success")
    void shouldReturn204_whenSuccess() throws Exception {
        doNothing().when(userService).delete(1L);
        mockMvc.perform(delete("/api/users/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} → 404 when not found")
    void shouldReturn404_whenUserNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("User not found: id=123"))
                .when(userService).delete(123L);
        mockMvc.perform(delete("/api/users/{id}", 123L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} → 409 on FK conflict")
    void shouldReturn409_whenIntegrityViolation() throws Exception {
        doThrow(new ResourceConflictException(Collections.singletonList("Cannot delete user id=7 due to related data.")))
                .when(userService).delete(7L);
        mockMvc.perform(delete("/api/users/{id}", 7L))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} → 400 when id is invalid")
    void shouldReturn400_whenInvalidId() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", 0L))
                .andExpect(status().isBadRequest());
    }
}
