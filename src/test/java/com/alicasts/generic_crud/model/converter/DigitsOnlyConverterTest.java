package com.alicasts.generic_crud.model.converter;

import com.alicasts.generic_crud.persistence.converter.DigitsOnlyConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DigitsOnlyConverterTest {

    private final DigitsOnlyConverter conv = new DigitsOnlyConverter();

    @Test
    void convertToDatabaseColumn_keepsOnlyDigits() {
        assertEquals("12345678901", conv.convertToDatabaseColumn("123.456.789-01"));
        assertEquals("88000000", conv.convertToDatabaseColumn("88.000-000"));
        assertEquals("", conv.convertToDatabaseColumn("abc-xyz"));
    }

    @Test
    void convertToDatabaseColumn_nullStaysNull() {
        assertNull(conv.convertToDatabaseColumn(null));
    }

    @Test
    void convertToEntityAttribute_identity() {
        assertEquals("001.002-003", conv.convertToEntityAttribute("001.002-003"));
    }
}
