package com.bookspin.util;

public class IsbnUtils {
    public static boolean isValidIsbn10(String isbn) {
        if (isbn == null) return false;
        String clean = isbn.replace("-", "").trim();
        if (clean.length() != 10) return false;
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            if (!Character.isDigit(clean.charAt(i))) return false;
            sum += (10 - i) * (clean.charAt(i) - '0');
        }
        char check = clean.charAt(9);
        sum += (check == 'X' ? 10 : Character.isDigit(check) ? check - '0' : -100);
        return sum % 11 == 0;
    }

    public static boolean isValidIsbn13(String isbn) {
        if (isbn == null) return false;
        String clean = isbn.replace("-", "").trim();
        if (clean.length() != 13 || !clean.matches("\\d+")) return false;
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = clean.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int check = (10 - (sum % 10)) % 10;
        return check == clean.charAt(12) - '0';
    }
}
