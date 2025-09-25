package com.alicasts.generic_crud.api;

import com.alicasts.generic_crud.api.dto.PageResponse;
import com.alicasts.generic_crud.api.dto.UserCreateRequestDTO;
import com.alicasts.generic_crud.api.dto.UserResponseDTO;
import com.alicasts.generic_crud.service.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO create(@Valid @RequestBody UserCreateRequestDTO dto,
                                  HttpServletResponse response) {
        UserResponseDTO created = userService.create(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        response.setHeader(HttpHeaders.LOCATION, location.toString());

        return created;
    }

    @GetMapping
    public PageResponse<UserResponseDTO> getAll(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return userService.findAll(pageable);
    }

    @GetMapping(value = "/{id:\\d+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponseDTO getById(@PathVariable Long id) {
        return userService.findById(id);
    }


    @GetMapping(value = "/by-email", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponseDTO getByEmail(@RequestParam @Email String email) {
        return userService.findByEmail(email);
    }
}
