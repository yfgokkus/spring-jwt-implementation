package com.example.jwtoken.dto;

import com.example.jwtoken.models.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

@Builder
public record CreateUserRequest(
        @NotBlank(message = "Name cannot be blank") String name,
        @NotBlank(message = "Username cannot be blank") String username,
        @NotBlank(message = "Password cannot be blank") String password,
        @NotNull(message = "Authorities cannot be null")
        @NotEmpty(message = "Authorities cannot be empty")
        Set<@Valid Role> authorities
) {}
