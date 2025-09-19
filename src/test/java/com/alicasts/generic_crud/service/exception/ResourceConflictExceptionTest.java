package com.alicasts.generic_crud.service.exception;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResourceConflictExceptionTest {

    @Test
    void singleField_constructor_setsFieldsWithOneItem() {
        var ex = new ResourceConflictException("email already exists", "email");
        assertEquals(List.of("email"), ex.getFields());
        assertEquals("email already exists", ex.getMessage());
    }

    @Test
    void listConstructor_setsFields_andGenericMessage() {
        var ex = new ResourceConflictException(List.of("email", "cpf"));
        assertEquals(List.of("email", "cpf"), ex.getFields());
        assertEquals("conflict", ex.getMessage());
    }
}
