package com.datasiqn.arcadia.player;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ArcadiaSender<T extends CommandSender> {
    private final T base;

    public ArcadiaSender(T base) {
        this.base = base;
    }

    public void sendMessage(String message) {
        sendMessage(ChatColor.GRAY + "[" + ChatColor.GOLD + "Arcadia" + ChatColor.GRAY + "] ", message);
    }
    public void sendMessage(String prefix, String message) {
        sendMessageRaw(prefix + message);
    }

    public void sendError(String error) {
        sendMessageRaw(ChatColor.RED + error);
    }

    public void sendMessageRaw(String message) {
        base.sendMessage(message);
    }

    public void sendDebugMessage(@Nullable Object message) {
        sendMessage(ChatColor.GREEN + "Debug > " + ChatColor.WHITE, message == null ? "null" : message.toString());
    }

    public T get() {
        return base;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArcadiaSender<?> that = (ArcadiaSender<?>) o;
        return base.equals(that.base);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base);
    }
}
