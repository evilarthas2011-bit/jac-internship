package com.kuleshov.userservice.service;

import com.kuleshov.userservice.dto.UserCreateRequest;
import com.kuleshov.userservice.dto.UserResponse;
import com.kuleshov.userservice.entity.User;
import com.kuleshov.userservice.exception.UserAlreadyExistsException;
import com.kuleshov.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        log.info("Attempting to create user with email: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            log.warn("User creation failed. Email already exists: {}", request.email());
            throw new UserAlreadyExistsException("User with email " + request.email() + " already exists");
        }

        User user = User.builder()
                .email(request.email())
                .password(request.password())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .build();

        User savedUser = userRepository.save(user);

        log.info("User successfully created with ID: {}", savedUser.getId());

        return new UserResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName()
        );
    }

    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName()))
                .toList();
}}