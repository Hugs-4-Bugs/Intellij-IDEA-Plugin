package com.PrabhatDevLab.services.ai;

public class QuotaGuard {

    private static long geminiRetryAfter = 0;

    public static boolean isGeminiBlocked() {
        return System.currentTimeMillis() < geminiRetryAfter;
    }

    public static void blockGeminiFor(long seconds) {
        geminiRetryAfter = System.currentTimeMillis() + (seconds * 1000);
    }
}
