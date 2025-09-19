package com.alicasts.generic_crud.service.impl;

import com.alicasts.generic_crud.api.dto.UserCreateRequestDTO;
import com.alicasts.generic_crud.api.dto.UserCreateResponseDTO;
import com.alicasts.generic_crud.model.User;
import com.alicasts.generic_crud.repository.UserRepository;
import com.alicasts.generic_crud.service.IUserService;
import com.alicasts.generic_crud.service.exception.ResourceConflictException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static com.alicasts.generic_crud.util.Normalizer.*;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserCreateResponseDTO create(UserCreateRequestDTO requestDTO) {

        String normalizedEmail = email(requestDTO.getEmail());
        String normalizedCpf   = digitsOnly(requestDTO.getCpf());

        ArrayList<String> conflicts = new ArrayList<String>();

        if (userRepository.existsByEmail(normalizedEmail)) conflicts.add("email");
        if (userRepository.existsByCpf(normalizedCpf)) conflicts.add("cpf");
        if (!conflicts.isEmpty()) throw new ResourceConflictException(conflicts);

        User user = new User(
                requestDTO.getName(),
                requestDTO.getEmail(),
                requestDTO.getAge(),
                requestDTO.getCpf(),
                requestDTO.getCep(),
                requestDTO.getAddress(),
                requestDTO.getSex()
        );

        try {
            User saved = userRepository.save(user);
            return new UserCreateResponseDTO(
                    saved.getId(),
                    saved.getName(),
                    saved.getEmail(),
                    saved.getAge(),
                    saved.getCpf(),
                    saved.getCep(),
                    saved.getAddress(),
                    saved.getSex(),
                    saved.getCreatedAt(),
                    saved.getUpdatedAt()
            );
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceConflictException("email or cpf already exists", null);
        }
    }
}
