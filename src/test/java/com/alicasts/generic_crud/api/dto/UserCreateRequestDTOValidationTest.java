package com.alicasts.generic_crud.api.dto;

import com.alicasts.generic_crud.model.Sex;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserCreateRequestDTOValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    private static UserCreateRequestDTO valid() {
        try {
            UserCreateRequestDTO dto = new UserCreateRequestDTO();
            set(dto, "name", "Ana Silva");
            set(dto, "email", "ana@example.com");
            set(dto, "age", 28);
            set(dto, "cpf", "12345678901");
            set(dto, "cep", "88000000");
            set(dto, "address", "Rua X, 100");
            set(dto, "sex", Sex.FEMALE);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Ex.", e);
        }
    }

    private static void set(Object target, String field, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }

    private static boolean hasViolation(Set<? extends ConstraintViolation<?>> v, String property) {
        return v.stream().anyMatch(cv -> property.equals(cv.getPropertyPath().toString()));
    }

    private static String repeat(char c, int n) {
        return String.valueOf(c).repeat(Math.max(0, n));
    }


    @Test
    void validDto_hasNoViolations() {
        Set<ConstraintViolation<UserCreateRequestDTO>> v = validator.validate(valid());
        assertTrue(v.isEmpty(), "Unreachable");
    }

    @Test
    void name_blank_triggersViolation() throws Exception {
        var dto = valid();
        set(dto, "name", " ");
        var v = validator.validate(dto);
        assertTrue(hasViolation(v, "name"));
    }

    @Test
    void email_invalid_triggersViolation() throws Exception {
        var dto = valid();
        set(dto, "email", "not-an-email");
        var v = validator.validate(dto);
        assertTrue(hasViolation(v, "email"));
    }

    @Test
    void age_outOfRange_triggersViolation() throws Exception {
        var dto = valid();
        set(dto, "age", 131);
        var vHigh = validator.validate(dto);
        assertTrue(hasViolation(vHigh, "age"));

        set(dto, "age", -1);
        var vLow = validator.validate(dto);
        assertTrue(hasViolation(vLow, "age"));
    }

    @Test
    void cpf_wrongPattern_triggersViolation() throws Exception {
        var dto = valid();
        set(dto, "cpf", "123");
        var v = validator.validate(dto);
        assertTrue(hasViolation(v, "cpf"));
    }

    @Test
    void cep_wrongPattern_triggersViolation() throws Exception {
        var dto = valid();
        set(dto, "cep", "88-000-000");
        var v = validator.validate(dto);
        assertTrue(hasViolation(v, "cep"));
    }

    @Test
    void address_blank_triggersViolation() throws Exception {
        var dto = valid();
        set(dto, "address", " ");
        var v = validator.validate(dto);
        assertTrue(hasViolation(v, "address"));
    }

    @Test
    void sex_null_triggersViolation() throws Exception {
        var dto = valid();
        set(dto, "sex", null);
        var v = validator.validate(dto);
        assertTrue(hasViolation(v, "sex"));
    }


    @Test
    void name_max120_ok_and_121_violation() throws Exception {
        var dto = valid();

        set(dto, "name", repeat('A', 120));
        var vOk = validator.validate(dto);
        assertFalse(hasViolation(vOk, "name"));

        set(dto, "name", repeat('A', 121));
        var vBad = validator.validate(dto);
        assertTrue(hasViolation(vBad, "name"));
    }

    @Test
    void address_max200_ok_and_201_violation() throws Exception {
        var dto = valid();

        set(dto, "address", repeat('X', 200));
        var vOk = validator.validate(dto);
        assertFalse(hasViolation(vOk, "address"));

        set(dto, "address", repeat('X', 201));
        var vBad = validator.validate(dto);
        assertTrue(hasViolation(vBad, "address"));
    }

    @Test
    void cpf_exact11_ok_and_other_lengths_violate() throws Exception {
        var dto = valid();

        set(dto, "cpf", "01234567890");
        var vOk = validator.validate(dto);
        assertFalse(hasViolation(vOk, "cpf"));

        set(dto, "cpf", "0123456789");
        var v10 = validator.validate(dto);
        assertTrue(hasViolation(v10, "cpf"));

        set(dto, "cpf", "012345678901");
        var v12 = validator.validate(dto);
        assertTrue(hasViolation(v12, "cpf"));
    }

    @Test
    void cep_exact8_ok_and_other_lengths_violate() throws Exception {
        var dto = valid();

        set(dto, "cep", "01234567");
        var vOk = validator.validate(dto);
        assertFalse(hasViolation(vOk, "cep"));

        set(dto, "cep", "0123456");
        var v7 = validator.validate(dto);
        assertTrue(hasViolation(v7, "cep"));

        set(dto, "cep", "012345678");
        var v9 = validator.validate(dto);
        assertTrue(hasViolation(v9, "cep"));
    }

    @Test
    void age_boundaries_0_and_130_ok_outside_violate() throws Exception {
        var dto = valid();

        set(dto, "age", 0);
        var v0 = validator.validate(dto);
        assertFalse(hasViolation(v0, "age"));

        set(dto, "age", 130);
        var v130 = validator.validate(dto);
        assertFalse(hasViolation(v130, "age"));

        set(dto, "age", -1);
        var vNeg = validator.validate(dto);
        assertTrue(hasViolation(vNeg, "age"));

        set(dto, "age", 131);
        var vHigh = validator.validate(dto);
        assertTrue(hasViolation(vHigh, "age"));
    }
}
