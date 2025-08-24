package com.aurafarmer.util;

import java.util.regex.Pattern;

// A utility class for validation logic.
public class ValidationUtils {

    private static final Pattern INPUT_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_@]{4,30}$"
    );

    public static boolean isValidInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        return INPUT_PATTERN.matcher(input).matches();
    }
}
