package com.alicasts.generic_crud.service.impl;

import com.alicasts.generic_crud.api.dto.PageResponse;
import com.alicasts.generic_crud.api.dto.UserCreateRequestDTO;
import com.alicasts.generic_crud.api.dto.UserResponseDTO;
import com.alicasts.generic_crud.api.mapper.UserMapper;
import com.alicasts.generic_crud.model.User;
import com.alicasts.generic_crud.repository.UserRepository;
import com.alicasts.generic_crud.service.IUserService;
import com.alicasts.generic_crud.service.exception.ResourceConflictException;
import com.alicasts.generic_crud.service.exception.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static com.alicasts.generic_crud.util.Normalizer.digitsOnly;
import static com.alicasts.generic_crud.util.Normalizer.email;

@Service
public class UserService implements IUserService {

    private static final int MAX_PAGE_SIZE = 100;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserResponseDTO create(UserCreateRequestDTO requestDTO) {


        String normalizedEmail = email(requestDTO.getEmail());
        String normalizedCpf   = digitsOnly(requestDTO.getCpf());

        final ArrayList<String> conflicts = new ArrayList<String>(2);
        if (userRepository.existsByEmail(normalizedEmail)) conflicts.add("email");
        if (userRepository.existsByCpf(normalizedCpf)) conflicts.add("cpf");
        if (!conflicts.isEmpty()) throw new ResourceConflictException(conflicts);

        final User user = new User(
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
            return userMapper.toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceConflictException("email or cpf already exists", null);
        }
    }

    @Override
    public PageResponse<UserResponseDTO> findAll(Pageable pageable) {
        int size = Math.min(Math.max(pageable.getPageSize(), 1), MAX_PAGE_SIZE);
        Pageable effective = PageRequest.of(
                Math.max(pageable.getPageNumber(), 0),
                size,
                pageable.getSort()
        );

        Page<User> page = userRepository.findAll(effective);

        var content = page.getContent().stream()
                .map(userMapper::toResponse)
                .toList();

        return PageResponse.from(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));
        return userMapper.toResponse(user);
    }
}
