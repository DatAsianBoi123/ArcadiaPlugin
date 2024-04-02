package com.datasiqn.arcadia.player;

import net.md_5.bungee.api.ChatColor;

public final class AttributeFormats {
    public static final AttributeFormat DAMAGE = AttributeFormat.withoutSign("Damage", ChatColor.RED, "✧");

    public static final AttributeFormat HEALTH = AttributeFormat.withSign("Health", ChatColor.RED, "♥");
    public static final AttributeFormat DEFENSE = AttributeFormat.withSign("Defense", ChatColor.GREEN, "❉");
    public static final AttributeFormat STRENGTH = AttributeFormat.withSign("Strength", ChatColor.DARK_RED, "⁂");
    public static final AttributeFormat ATTACK_SPEED = AttributeFormat.withSign("Attack Speed", ChatColor.YELLOW, "⚔");
    public static final AttributeFormat HUNGER = AttributeFormat.withoutSign("Max Hunger", ChatColor.of("#743600"), "🍖");
    public static final AttributeFormat SPEED = AttributeFormat.withSign("Speed", ChatColor.WHITE, "☄");

    private AttributeFormats() {
        throw new AssertionError();
    }
}
