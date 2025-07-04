package com.example.jwtoken.service;

import com.example.jwtoken.dto.CreateUserRequest;
import com.example.jwtoken.exception.user.UserCreationException;
import com.example.jwtoken.exception.user.UsernameAlreadyExistsException;
import com.example.jwtoken.models.User;
import com.example.jwtoken.repo.UserRepo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User createUser(CreateUserRequest request) {
        if (userRepo.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(request.username());
        }

        User newUser = User.builder()
                .name(request.name())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .authorities(request.authorities())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();

        try {
            return userRepo.save(newUser);
        } catch (DataIntegrityViolationException e) {
            throw new UserCreationException("Failed to save user to database", e);
        }
    }
}

