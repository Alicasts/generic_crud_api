package com.alicasts.generic_crud.api;

import com.alicasts.generic_crud.api.dto.PageResponse;
import com.alicasts.generic_crud.api.dto.UserResponseDTO;
import com.alicasts.generic_crud.model.Sex;
import com.alicasts.generic_crud.service.IUserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerGetAllTest {

    @Autowired MockMvc mvc;
    @MockitoBean IUserService userService;

    @Test
    void getAll_defaultPageable_andResponseOk() throws Exception {
        var u = new UserResponseDTO(1L, "Ana", "ana@example.com", 28, "123", "88000000",
                "Rua X", Sex.FEMALE, Instant.parse("2025-09-16T13:00:00Z"), Instant.parse("2025-09-16T13:00:00Z"));
        when(userService.findAll(any())).thenReturn(new PageResponse<>(
                List.of(u), 0, 20, 1L, 1, true, true, "createdAt: DESC"));

        mvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.sort").value("createdAt: DESC"));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userService).findAll(captor.capture());
        var pageable = captor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(20);
        assertThat(pageable.getSort().toString()).containsIgnoringCase("createdAt: DESC");
    }

    @Test
    void getAll_customPageable_paramsArePassedToService() throws Exception {
        when(userService.findAll(any())).thenReturn(new PageResponse<>(
                List.of(), 2, 5, 0, 0, false, true, "name: ASC, createdAt: DESC"));

        mvc.perform(get("/api/users?page=2&size=5&sort=name,asc&sort=createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.size").value(5));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userService).findAll(captor.capture());
        var pageable = captor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(2);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().toString()).containsIgnoringCase("name: ASC");
        assertThat(pageable.getSort().toString()).containsIgnoringCase("createdAt: DESC");
    }
}
