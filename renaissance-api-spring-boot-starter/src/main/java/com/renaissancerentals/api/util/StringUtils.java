package com.renaissancerentals.api.util;

import java.util.Arrays;

public final class StringUtils {

    public static String capitalizeWords(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }

        StringBuilder sb = new StringBuilder(input.length());
        boolean capitalizeNext = true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                sb.append(c);
            } else if (capitalizeNext) {
                sb.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }

        return sb.toString();
    }

    public static String getFirstName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "";
        }
        return Arrays.stream(fullName.trim().split("\\s+")).findFirst().orElse("");
    }
}
