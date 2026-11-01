package com.kuleshov.userservice.service;

import com.kuleshov.userservice.dto.UserCreateRequest;
import com.kuleshov.userservice.dto.UserResponse;
import com.kuleshov.userservice.entity.User;
import com.kuleshov.userservice.exception.UserAlreadyExistsException;
import com.kuleshov.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_Success() {
        UserCreateRequest request = new UserCreateRequest("test@test.com", "password123", "John", "Doe");

        when(userRepository.existsByEmail(request.email())).thenReturn(false);

        User savedUser = User.builder()
                .id(1L)
                .email(request.email())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("test@test.com", response.email());

        verify(userRepository, times(1)).existsByEmail(request.email());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsException() {
        UserCreateRequest request = new UserCreateRequest("exists@test.com", "password123", "John", "Doe");

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(request)
        );

        assertEquals("User with email exists@test.com already exists", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }
}