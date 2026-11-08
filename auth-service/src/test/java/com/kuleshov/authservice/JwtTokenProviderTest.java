package com.kuleshov.authservice;

import com.kuleshov.authservice.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret",
                "YourSuperSecretKeyThatIsAtLeast32CharactersLong!");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", 3600000L);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String email = "test@test.com";

        String token = jwtTokenProvider.generateToken(email);

        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
        assertTrue(token.contains("."), "Token should have 3 parts separated by dots");
    }

    @Test
    void getEmailFromToken_ShouldReturnCorrectEmail() {
        String email = "test@test.com";
        String token = jwtTokenProvider.generateToken(email);
        String extractedEmail = jwtTokenProvider.getEmailFromToken(token);

        assertEquals(email, extractedEmail, "Extracted email should match original");
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        String email = "test@test.com";
        String token = jwtTokenProvider.generateToken(email);
        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid, "Valid token should return true");
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        String invalidToken = "invalid.token.here";
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        assertFalse(isValid, "Invalid token should return false");
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        JwtTokenProvider expiredProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(expiredProvider, "jwtSecret",
                "YourSuperSecretKeyThatIsAtLeast32CharactersLong!");
        ReflectionTestUtils.setField(expiredProvider, "jwtExpiration", -1000L);
        String token = expiredProvider.generateToken("test@test.com");
        boolean isValid = jwtTokenProvider.validateToken(token);

        assertFalse(isValid, "Expired token should return false");
    }
}