package com.alicasts.generic_crud.api;

import com.alicasts.generic_crud.api.dto.UserCreateRequestDTO;
import com.alicasts.generic_crud.api.dto.UserCreateResponseDTO;
import com.alicasts.generic_crud.service.IUserService;
import com.alicasts.generic_crud.service.impl.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final IUserService service;

    public UserController(IUserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserCreateResponseDTO> create(@Valid @RequestBody UserCreateRequestDTO body,
                                               UriComponentsBuilder uriBuilder) {
        UserCreateResponseDTO created = service.create(body);
        return ResponseEntity
                .created(uriBuilder.path("/api/users/{id}").buildAndExpand(created.getId()).toUri())
                .body(created);
    }
}
