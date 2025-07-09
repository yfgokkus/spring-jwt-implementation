package com.example.jwtoken.controller;

import com.example.jwtoken.mapper.SystemMapper;
import com.example.jwtoken.dto.*;
import com.example.jwtoken.models.User;
import com.example.jwtoken.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final SystemMapper mapper;

    public UserController(UserService userService, SystemMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to Spring Security JWT Token";
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        CreateUserResponse response = mapper.toCreateUserResponse(user);

        log.info("User '{}' successfully created", request.username());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user")
    public String getUserString(){
        return "This is the User";
    }

    @GetMapping("/admin")
    public String getAdminString(){
        return "This is the Admin";
    }

}

