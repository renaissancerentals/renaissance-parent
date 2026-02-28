package com.renaissancerentals.foundation.text.util;

public final class TextUtils {
    public static String cleanPhoneNumber(String phoneNumber){
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return phoneNumber;
        }

        // Remove all non-digits
        String digitsOnly = phoneNumber.replaceAll("\\D","");

        // Remove leading 1 if total length is 11 (North America format)
        if (digitsOnly.length() == 11 && digitsOnly.startsWith("1")) {
            return digitsOnly.substring(1);
        }

        return digitsOnly;
    }
}
