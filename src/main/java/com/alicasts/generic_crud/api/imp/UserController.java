package com.alicasts.generic_crud.api.imp;

import com.alicasts.generic_crud.api.IUserController;
import com.alicasts.generic_crud.api.dto.PageResponse;
import com.alicasts.generic_crud.api.dto.UserCreateRequestDTO;
import com.alicasts.generic_crud.api.dto.UserResponseDTO;
import com.alicasts.generic_crud.api.dto.UserUpdateRequest;
import com.alicasts.generic_crud.service.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController implements IUserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @Override
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

    @Override
    @GetMapping
    public PageResponse<UserResponseDTO> getAll(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return userService.findAll(pageable);
    }

    @Override
    @GetMapping(value = "/{id:\\d+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponseDTO getById(@PathVariable Long id) {
        return userService.findById(id);
    }


    @Override
    @GetMapping(value = "/by-email", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserResponseDTO getByEmail(@RequestParam @Email String email) {
        return userService.findByEmail(email);
    }

    @Override
    @PutMapping("/{id}")
    public UserResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest requestData) {
        return userService.update(id, requestData);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Min(1) Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
