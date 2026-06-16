package com.grupo.learningmore.security;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordValidatorTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static record TestDto(@ValidPassword String password) {}

    @Test
    public void testValidPassword() {
        TestDto dto = new TestDto("StrongPass123!");
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Password should be valid");
    }

    @Test
    public void testPasswordWithContextWord() {
        TestDto dto = new TestDto("LearningMore123");
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Password containing 'learningmore' should be invalid");
    }

    @Test
    public void testPasswordWithIsep() {
        TestDto dto = new TestDto("isep_password");
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Password containing 'isep' should be invalid");
    }

    @Test
    public void testPasswordWithDesofs() {
        TestDto dto = new TestDto("my_desofs_pass");
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Password containing 'desofs' should be invalid");
    }

    @Test
    public void testCommonPassword() {
        TestDto dto = new TestDto("qwerty12345");
        Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Common password 'qwerty' should be invalid");
    }
}
