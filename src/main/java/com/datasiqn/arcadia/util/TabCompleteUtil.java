package com.datasiqn.arcadia.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public abstract class TabCompleteUtil {
    public static void sort(@Nullable List<String> list) {
        if (list == null) return;
        list.sort(Comparator.naturalOrder());
    }

    public static void filterStartsWith(@Nullable List<String> list, @NotNull String str) {
        if (list == null) return;
        List<String> prevList = new ArrayList<>(list);
        list.clear();
        list.addAll(prevList.stream().filter(s -> s.toUpperCase(Locale.ROOT).startsWith(str.toUpperCase(Locale.ROOT))).toList());
    }
}
