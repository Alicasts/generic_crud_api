package com.alicasts.generic_crud.service;

import com.alicasts.generic_crud.api.dto.UserCreateRequestDTO;
import com.alicasts.generic_crud.api.dto.UserCreateResponseDTO;

public interface IUserService {

    UserCreateResponseDTO create(UserCreateRequestDTO r);

}
