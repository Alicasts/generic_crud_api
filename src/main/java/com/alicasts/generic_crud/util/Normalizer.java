package com.alicasts.generic_crud.util;

public final class Normalizer {
    private Normalizer() {}

    public static String trim(String rawString) {
        return rawString == null ? null : rawString.trim();
    }

    public static String email(String rawEmail) {
        rawEmail = trim(rawEmail);
        return rawEmail == null ? null : rawEmail.toLowerCase();
    }

    public static String digitsOnly(String rawDigits) {
        return rawDigits == null ? null : rawDigits.replaceAll("\\D", "");
    }
}
