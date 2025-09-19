package com.alicasts.generic_crud.model.converter;

import com.alicasts.generic_crud.persistence.converter.EmailLowercaseConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailLowercaseConverterTest {

    private final EmailLowercaseConverter conv = new EmailLowercaseConverter();

    @Test
    void convertToDatabaseColumn_lowercasesAndTrims() {
        assertEquals("ana.silva+test@example.com",
                conv.convertToDatabaseColumn("  Ana.SILVA+TEST@example.com  "));
    }

    @Test
    void convertToDatabaseColumn_nullStaysNull() {
        assertNull(conv.convertToDatabaseColumn(null));
    }

    @Test
    void convertToEntityAttribute_identity() {
        assertEquals("Ana@Example.com", conv.convertToEntityAttribute("Ana@Example.com"));
    }
}
