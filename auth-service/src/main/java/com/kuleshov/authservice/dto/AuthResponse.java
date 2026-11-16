package com.kuleshov.authservice.dto;

public record AuthResponse(
        String token,
        String email
) {}