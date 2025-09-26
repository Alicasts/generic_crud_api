package com.alicasts.generic_crud.service;

import com.alicasts.generic_crud.api.dto.PageResponse;
import com.alicasts.generic_crud.api.dto.UserCreateRequestDTO;
import com.alicasts.generic_crud.api.dto.UserResponseDTO;
import com.alicasts.generic_crud.api.dto.UserUpdateRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface IUserService {

    UserResponseDTO create(UserCreateRequestDTO r);
    PageResponse<UserResponseDTO> findAll(Pageable pageable);
    UserResponseDTO findById(Long id);
    UserResponseDTO findByEmail(String email);
    UserResponseDTO update(Long id, UserUpdateRequest requestData);
}
