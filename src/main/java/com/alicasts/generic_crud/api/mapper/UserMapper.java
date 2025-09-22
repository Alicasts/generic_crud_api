package com.alicasts.generic_crud.api.mapper;

import com.alicasts.generic_crud.api.dto.UserCreateRequestDTO;
import com.alicasts.generic_crud.api.dto.UserResponseDTO;
import com.alicasts.generic_crud.model.User;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = NormalizerMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserMapper {

    @Mapping(target = "email", qualifiedByName = "normalizeEmail")
    @Mapping(target = "cpf",   qualifiedByName = "digitsOnly")
    User toEntity(UserCreateRequestDTO dto);

    public UserResponseDTO toResponse(User user);
}
