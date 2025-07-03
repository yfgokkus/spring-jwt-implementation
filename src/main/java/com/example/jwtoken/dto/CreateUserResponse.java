package com.example.jwtoken.dto;

import java.util.List;

public record CreateUserResponse(
        Long id,
        String name,
        String username,
        List<String> roles
) {}