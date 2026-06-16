package com.grupo.learningmore.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class BreachedPasswordServiceIntegrationTest {

    @Autowired
    private BreachedPasswordService service;

    @Test
    public void testKnownBreachedPassword() {
        // "password" is definitely breached
        assertTrue(service.isPasswordBreached("password"), "Password 'password' should be detected as breached");
    }

    @Test
    public void testLikelySafePassword() {
        // A very random and unique password
        assertFalse(service.isPasswordBreached("ThisIsAVeryUniquePassword!@#1234567890_Qwertyuiop"), "Unique password should not be detected as breached");
    }
}
