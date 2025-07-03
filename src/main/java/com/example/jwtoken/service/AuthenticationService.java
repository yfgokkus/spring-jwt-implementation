package com.example.jwtoken.service;

import com.example.jwtoken.dto.AuthRequest;
import com.example.jwtoken.dto.AuthResponse;
import com.example.jwtoken.dto.RefreshTokenRequest;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService ;
    private final UserService userService;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtService jwtService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticate(request);
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return generateTokens(user);
    }

    private Authentication authenticate(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("User '{}' successfully authenticated", request.username());
        return authentication;
    }

    private AuthResponse generateTokens(UserDetails user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        log.debug("Generated access and refresh tokens for user '{}'", user.getUsername());
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        try {
            String username = jwtService.extractUsername(refreshToken); // validate and extract
            UserDetails user = userService.loadUserByUsername(username);

            if (jwtService.validateToken(refreshToken, user)) {
                String newAccessToken = jwtService.generateAccessToken(user);
                return new AuthResponse(newAccessToken, refreshToken); // or generate a new refresh token
            } else {
                throw new RuntimeException("Invalid refresh token");
            }

        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Refresh token expired");
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token");
        }
    }
}
