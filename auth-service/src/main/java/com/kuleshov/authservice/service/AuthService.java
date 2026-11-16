package com.kuleshov.authservice.service;

import com.kuleshov.authservice.dto.AuthResponse;
import com.kuleshov.authservice.dto.LoginRequest;
import com.kuleshov.authservice.dto.RegisterRequest;
import com.kuleshov.authservice.entity.User;
import com.kuleshov.authservice.enums.Role;
import com.kuleshov.authservice.exception.EmailAlreadyExistsException;
import com.kuleshov.authservice.repository.UserRepository;
import com.kuleshov.authservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.email());
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Registration failed. Email already exists: {}", request.email());
            throw new EmailAlreadyExistsException(
                    "User with email " + request.email() + " already exists"
            );
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User successfully registered with ID: {}", savedUser.getId());

        String token = jwtTokenProvider.generateToken(savedUser.getEmail());
        log.info("JWT token generated for user: {}", savedUser.getEmail());

        return new AuthResponse(token, savedUser.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.email());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (BadCredentialsException e) {
            log.warn("Login failed. Invalid credentials for email: {}", request.email());
            throw new BadCredentialsException("Invalid email or password");
        }
        String token = jwtTokenProvider.generateToken(request.email());
        log.info("Login successful for email: {}", request.email());

        return new AuthResponse(token, request.email());
    }
}