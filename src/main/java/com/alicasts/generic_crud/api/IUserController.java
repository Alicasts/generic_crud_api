package com.alicasts.generic_crud.api;

import com.alicasts.generic_crud.api.dto.PageResponse;
import com.alicasts.generic_crud.api.dto.UserCreateRequestDTO;
import com.alicasts.generic_crud.api.dto.UserResponseDTO;
import com.alicasts.generic_crud.api.dto.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "Users", description = "User management endpoints")
public interface IUserController {

    @Operation(
            summary = "Create user",
            description = "Creates a new user after validation and uniqueness checks (email/CPF). " +
                    "Returns 201 with Location header pointing to the new resource."
    )
    UserResponseDTO create(
            @RequestBody(
                    required = true,
                    description = "Payload to create a new user",
                    content = @Content(schema = @Schema(implementation = UserCreateRequestDTO.class))
            )
            @Valid UserCreateRequestDTO dto,
            @Parameter(hidden = true) HttpServletResponse response
    );

    @Operation(summary = "List users (paginated)",
            description = """
        Returns a paginated list of users. Default page size is 20, sorted by createdAt DESC.
        You can pass multiple 'sort' parameters, e.g.: sort=name,ASC&sort=createdAt,DESC
      """)
    PageResponse<UserResponseDTO> getAll(@ParameterObject Pageable pageable);

    @Operation(summary = "Get user by ID", description = "Fetch a user by numeric ID.")
    UserResponseDTO getById(
            @Parameter(description = "User ID (numeric)", example = "1")
            Long id
    );

    @Operation(summary = "Get user by email", description = "Fetch a user by email address.")
    UserResponseDTO getByEmail(
            @Parameter(description = "Email address", example = "jane.doe@example.com")
            @Email String email
    );

    @Operation(summary = "Update user", description = "Updates user data by ID.")
    UserResponseDTO update(
            @Parameter(description = "User ID (numeric)", example = "1") Long id,
            @RequestBody(
                    required = true,
                    description = "Payload to update an existing user",
                    content = @Content(schema = @Schema(implementation = UserUpdateRequest.class))
            )
            @Valid UserUpdateRequest requestData
    );

    @Operation(summary = "Delete user", description = "Deletes a user by ID.")
    ResponseEntity<Void> delete(
            @Parameter(description = "User ID (numeric, ≥ 1)", example = "1")
            @Min(1) Long id
    );
}
