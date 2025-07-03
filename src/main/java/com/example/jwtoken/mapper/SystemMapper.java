package com.example.jwtoken.mapper;

import com.example.jwtoken.dto.CreateUserResponse;
import com.example.jwtoken.models.Role;
import com.example.jwtoken.models.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SystemMapper {

    public CreateUserResponse toCreateUserResponse(User user) {
        List<String> roleNames = user.getAuthorities().stream()
                .map(Role::getAuthority)
                .collect(Collectors.toList());

        return new CreateUserResponse(
                user.getId(),
                user.getName(),
                user.getUsername(),
                roleNames
        );
    }

}