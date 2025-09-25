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
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerGetInvalidParamsTest {

    @Autowired MockMvc mvc;
    @MockitoBean IUserService userService;

    private static PageResponse<UserResponseDTO> dummyPage() {
        var u = new UserResponseDTO(1L, "Ana", "ana@example.com", 28,
                "12345678901", "88000000", "Rua X, 100", Sex.FEMALE,
                Instant.parse("2025-09-16T13:00:00Z"), Instant.parse("2025-09-16T13:00:00Z"));
        return new PageResponse<>(List.of(u), 0, 20, 1, 1, true, true, "");
    }

    @Test
    void getAll_invalidPage_nonNumeric_fallsBackToDefaults() throws Exception {
        when(userService.findAll(any())).thenReturn(dummyPage());

        mvc.perform(get("/api/users?page=abc").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userService).findAll(captor.capture());
        var pageable = captor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(20);
    }

    @Test
    void getAll_invalidSize_nonNumeric_fallsBackToDefaults() throws Exception {
        when(userService.findAll(any())).thenReturn(dummyPage());

        mvc.perform(get("/api/users?size=foo").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userService).findAll(captor.capture());
        var pageable = captor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(20);
    }

    @Test
    void getAll_invalidSortDirection_isParsedAsSecondPropertyAsc() throws Exception {
        when(userService.findAll(any())).thenReturn(dummyPage());

        mvc.perform(get("/api/users?sort=id,down").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userService).findAll(captor.capture());
        var sort = captor.getValue().getSort();
        Sort.Order idOrder = sort.getOrderFor("id");
        Sort.Order downOrder = sort.getOrderFor("down");
        assertThat(idOrder).isNotNull();
        assertThat(idOrder.getDirection()).isEqualTo(Sort.Direction.ASC);
        assertThat(downOrder).isNotNull();
        assertThat(downOrder.getDirection()).isEqualTo(Sort.Direction.ASC);
    }
}
