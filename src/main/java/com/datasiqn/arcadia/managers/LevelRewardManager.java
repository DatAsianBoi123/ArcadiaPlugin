package com.datasiqn.arcadia.managers;

import com.datasiqn.arcadia.util.lorebuilder.component.LoreComponent;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.List;

public class LevelRewardManager {
    private final ListMultimap<Integer, String> rewards = ArrayListMultimap.create();

    public void addReward(int level, @NotNull LoreComponent component) {
        rewards.put(level, component.toString());
    }

    @UnmodifiableView
    public List<String> getRewards(int level) {
        return Collections.unmodifiableList(rewards.get(level));
    }
}
