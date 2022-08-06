package com.datasiqn.arcadia.items.abilities;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemAbility {
    private final String abilityName;
    private final List<String> description;
    private final AbilityType type;
    private final int cooldown;
    private final Consumer<AbilityExecutor> executor;

    public ItemAbility(String abilityName, @NotNull List<String> description, AbilityType type, int cooldown, Consumer<AbilityExecutor> executor) {
        this.abilityName = abilityName;
        this.description = description.stream().map(s -> ChatColor.RESET + "" + ChatColor.GRAY + s).collect(Collectors.toList());
        this.type = type;
        this.cooldown = cooldown;
        this.executor = executor;
    }

    public int getCooldown() {
        return cooldown;
    }

    public AbilityType getType() {
        return type;
    }

    public List<String> asLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "Item Ability: " + ChatColor.WHITE + abilityName + " " + type.toString());
        lore.addAll(description);
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        lore.add(ChatColor.RESET + "" + ChatColor.DARK_GRAY + "Cooldown: " + ChatColor.GREEN + decimalFormat.format(cooldown / 20) + "s");
        return lore;
    }

    public void execute(@NotNull AbilityExecutor abilityExecutor) {
        executor.accept(abilityExecutor);
    }
}
