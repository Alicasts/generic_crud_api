package com.alicasts.generic_crud.api.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import static com.alicasts.generic_crud.util.Normalizer.digitsOnly;
import static com.alicasts.generic_crud.util.Normalizer.email;

@Component
public class NormalizerMapper {
    @Named("normalizeEmail")
    public String normalizeEmail(String s) { return email(s); }

    @Named("digitsOnly")
    public String digitsOnlyStr(String s) { return digitsOnly(s); }
}
