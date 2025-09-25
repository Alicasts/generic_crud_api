package com.alicasts.generic_crud.service;

import com.alicasts.generic_crud.api.dto.PageResponse;
import com.alicasts.generic_crud.api.dto.UserCreateRequestDTO;
import com.alicasts.generic_crud.api.dto.UserResponseDTO;
import org.springframework.data.domain.Pageable;

public interface IUserService {

    UserResponseDTO create(UserCreateRequestDTO r);
    PageResponse<UserResponseDTO> findAll(Pageable pageable);
    UserResponseDTO findById(Long id);
    UserResponseDTO findByEmail(String email);
}
