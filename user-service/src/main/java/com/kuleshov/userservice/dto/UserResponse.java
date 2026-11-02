package com.kuleshov.userservice.dto;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName
) {}