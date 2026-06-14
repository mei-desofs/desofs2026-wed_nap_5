package com.grupo.learningmore.security;


 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(properties = {
    "security.jwt.secret=this-is-a-secure-test-secret-key-with-32-chars-minimum",
    "security.jwt.expiration=86400000"
})
@ActiveProfiles("test")class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                "this-is-a-secure-test-secret-key-with-32-chars-minimum"
        );
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);
    }

    @Test
    void generateTokenCreatesValidToken() {
        String token = jwtService.generateToken("user-id-123", "STUDENT", 0L);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void extractUsernameReturnsSubject() {
        String token = jwtService.generateToken("user-id-123", "STUDENT", 0L);

        assertEquals("user-id-123", jwtService.extractUsername(token));
    }

    @Test
    void extractRoleReturnsRoleClaim() {
        String token = jwtService.generateToken("user-id-123", "ADMIN", 0L);

        assertEquals("ADMIN", jwtService.extractRole(token));
    }

    @Test
    void invalidTokenReturnsFalse() {
        assertFalse(jwtService.isTokenValid("invalid-token"));
    }

    @Test
    void expiredTokenReturnsFalse() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);

        String token = jwtService.generateToken("user-id-123", "STUDENT", 0L);

        assertFalse(jwtService.isTokenValid(token));
    }
}
