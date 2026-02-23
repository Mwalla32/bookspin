package com.bookspin.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static final String FILE = "bookspin_prefs";

    public static boolean isLookupEnabled(Context context) {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean("lookup_enabled", true);
    }

    public static void setLookupEnabled(Context context, boolean enabled) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putBoolean("lookup_enabled", enabled).apply();
    }

    public static int avoidRecentCount(Context context) {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getInt("avoid_recent", 5);
    }

    public static void setAvoidRecentCount(Context context, int count) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putInt("avoid_recent", count).apply();
    }

    public static boolean skippedLogin(Context context) {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getBoolean("skipped_login", false);
    }

    public static void setSkippedLogin(Context context, boolean skipped) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().putBoolean("skipped_login", skipped).apply();
    }
}
