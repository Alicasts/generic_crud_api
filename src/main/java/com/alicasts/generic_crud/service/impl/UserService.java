package com.alicasts.generic_crud.service.impl;

import com.alicasts.generic_crud.api.dto.PageResponse;
import com.alicasts.generic_crud.api.dto.UserCreateRequestDTO;
import com.alicasts.generic_crud.api.dto.UserResponseDTO;
import com.alicasts.generic_crud.api.dto.UserUpdateRequest;
import com.alicasts.generic_crud.api.mapper.UserMapper;
import com.alicasts.generic_crud.api.mapper.UserPatchMapper;
import com.alicasts.generic_crud.model.User;
import com.alicasts.generic_crud.repository.UserRepository;
import com.alicasts.generic_crud.service.IUserService;
import com.alicasts.generic_crud.service.exception.ResourceConflictException;
import com.alicasts.generic_crud.service.exception.ResourceNotFoundException;
import com.alicasts.generic_crud.service.guard.UserUniquenessGuard;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static com.alicasts.generic_crud.util.Normalizer.email;

@Service
public class UserService implements IUserService {

    private static final int MAX_PAGE_SIZE = 100;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserPatchMapper userPatchMapper;
    private final UserUniquenessGuard uniquenessGuard;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper,
                       UserPatchMapper userPatchMapper,
                       UserUniquenessGuard uniquenessGuard) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userPatchMapper = userPatchMapper;
        this.uniquenessGuard = uniquenessGuard;

    }

    @Override
    @Transactional
    public UserResponseDTO create(UserCreateRequestDTO req) {
        uniquenessGuard.checkOnCreate(req.getEmail(), req.getCpf());

        try {
            User saved = userRepository.save(userMapper.toEntity(req));
            return userMapper.toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceConflictException("email or cpf already exists, DataIntegrityViolationException", null);
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

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findByEmail(String rawEmail) {
        final String normalized = email(rawEmail);
        var user = userRepository.findByEmailIgnoreCase(normalized)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponseDTO update(Long id, UserUpdateRequest req) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        if (req.cpf() != null) {
            uniquenessGuard.checkCpfOnUpdate(req.cpf(), user.getCpf());
        }

        var patch = userPatchMapper.toPatch(req);
        user.apply(patch);

        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: id=" + id);
        }
        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceConflictException(Collections.singletonList("Cannot delete user id=" + id + " due to related data."));
        }
    }
}
