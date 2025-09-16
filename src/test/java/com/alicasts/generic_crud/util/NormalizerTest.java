package com.alicasts.generic_crud.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NormalizerTest {

    @Test
    void email_trimsAndLowercases() {
        assertEquals("ana@example.com", Normalizer.email("  Ana@Example.com "));
        assertEquals("ana.silva+test@example.com", Normalizer.email("Ana.SILVA+TEST@example.com"));
        assertNull(Normalizer.email(null));
    }

    @Test
    void digitsOnly_stripsNonDigits() {
        assertEquals("12345678901", Normalizer.digitsOnly("123.456.789-01"));
        assertEquals("88000000", Normalizer.digitsOnly("88.000-000"));
        assertEquals("", Normalizer.digitsOnly("abc-xyz"));
        assertNull(null);
    }

    @Test
    void trim_handlesNullAndSpaces() {
        assertEquals("Rua X", Normalizer.trim("  Rua X  "));
        assertEquals("", Normalizer.trim("   "));
        assertNull(null);
    }
}
