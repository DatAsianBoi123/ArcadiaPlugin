package com.datasiqn.arcadia.util;

public final class XPUtil {
    private XPUtil() {}

    public static long getTotalXPAtLevel(int level) {
        return (long) (100 * Math.pow(level, 2));
    }

    public static long getXPForLevel(int level) {
        return (long) 200 * (level + 1) - 100;
    }

    public static int getLevelFromXP(long xp) {
        return (int) Math.floor(Math.sqrt(xp / 100d));
    }

    public static double getProgress(long xp) {
        int level = getLevelFromXP(xp);
        return (xp - getTotalXPAtLevel(level)) / (double) getXPForLevel(level);
    }
}
