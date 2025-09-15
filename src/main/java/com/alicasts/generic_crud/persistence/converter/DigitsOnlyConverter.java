package com.alicasts.generic_crud.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class DigitsOnlyConverter implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        return attribute.replaceAll("\\D", "");
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData;
    }
}
