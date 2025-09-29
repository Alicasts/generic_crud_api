package com.alicasts.generic_crud.service.guard;

import com.alicasts.generic_crud.repository.UserRepository;
import com.alicasts.generic_crud.service.exception.ResourceConflictException;
import com.alicasts.generic_crud.util.Normalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserUniquenessGuardTest {

    private UserRepository userRepository;
    private UserUniquenessGuard guard;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        guard = new UserUniquenessGuard(userRepository);
    }

    @Test
    void checkOnCreate_shouldPass_whenNoConflicts() {
        when(userRepository.existsByEmail(Normalizer.email("X@Email.com"))).thenReturn(false);
        when(userRepository.existsByCpf(Normalizer.digitsOnly("123.456.789-01"))).thenReturn(false);

        assertDoesNotThrow(() -> guard.checkOnCreate("X@Email.com", "123.456.789-01"));
    }

    @Test
    void checkOnCreate_shouldFail_whenEmailConflict() {
        when(userRepository.existsByEmail(Normalizer.email("a@a.com"))).thenReturn(true);
        when(userRepository.existsByCpf(Normalizer.digitsOnly("12345678901"))).thenReturn(false);

        var ex = assertThrows(ResourceConflictException.class,
                () -> guard.checkOnCreate("a@a.com", "12345678901"));

        assertNotNull(ex.getFields());
        assertEquals(List.of("email"), ex.getFields());
    }

    @Test
    void checkOnCreate_shouldFail_whenCpfConflict() {
        when(userRepository.existsByEmail(Normalizer.email("a@a.com"))).thenReturn(false);
        when(userRepository.existsByCpf(Normalizer.digitsOnly("12345678901"))).thenReturn(true);

        var ex = assertThrows(ResourceConflictException.class,
                () -> guard.checkOnCreate("a@a.com", "12345678901"));

        assertNotNull(ex.getFields());
        assertEquals(java.util.List.of("cpf"), ex.getFields());
    }

    @Test
    void checkOnCreate_shouldFail_whenBothConflict() {
        when(userRepository.existsByEmail(Normalizer.email("a@a.com"))).thenReturn(true);
        when(userRepository.existsByCpf(Normalizer.digitsOnly("12345678901"))).thenReturn(true);

        var ex = assertThrows(ResourceConflictException.class,
                () -> guard.checkOnCreate("a@a.com", "12345678901"));
        assertNotNull(ex);
    }

    @Test
    void checkCpfOnUpdate_shouldIgnore_whenNewCpfIsNull() {
        assertDoesNotThrow(() -> guard.checkCpfOnUpdate(null, "12345678901"));
        verifyNoInteractions(userRepository);
    }

    @Test
    void checkCpfOnUpdate_shouldPass_whenCpfUnchanged() {
        assertDoesNotThrow(() -> guard.checkCpfOnUpdate("123.456.789-01", "12345678901"));
        verifyNoInteractions(userRepository);
    }

    @Test
    void checkCpfOnUpdate_shouldFail_whenNewCpfConflicts() {
        when(userRepository.existsByCpf("99999999999")).thenReturn(true);

        var ex = assertThrows(ResourceConflictException.class,
                () -> guard.checkCpfOnUpdate("999.999.999-99", "12345678901"));

        assertNotNull(ex.getFields());
        assertEquals(java.util.List.of("cpf"), ex.getFields());
    }

    @Test
    void checkCpfOnUpdate_shouldPass_whenNewCpfFree() {
        when(userRepository.existsByCpf("99999999999")).thenReturn(false);

        assertDoesNotThrow(() -> guard.checkCpfOnUpdate("999.999.999-99", "12345678901"));
    }
}
