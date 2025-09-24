package com.alicasts.generic_crud.service.exception;

import java.io.Serial;
import java.util.List;

public class ResourceConflictException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    private final List<String> fields;

    public ResourceConflictException(String message, String field) {
        super(message);
        this.fields = (field == null) ? List.of() : List.of(field);
    }

    public ResourceConflictException(List<String> fields) {
        super("conflict");
        this.fields = List.copyOf(fields);
    }

    public List<String> getFields() { return fields; }
}
