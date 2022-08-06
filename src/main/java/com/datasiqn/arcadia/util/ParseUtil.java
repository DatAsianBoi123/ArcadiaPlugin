package com.datasiqn.arcadia.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ParseUtil {
    @Nullable
    public static Integer parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Nullable
    public static Boolean parseBoolean(@NotNull String str) {
        if (str.equalsIgnoreCase("true")) return true;
        else if (str.equalsIgnoreCase("false")) return false;
        else return null;
    }
}
