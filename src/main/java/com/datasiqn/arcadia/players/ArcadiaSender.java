package com.datasiqn.arcadia.players;

import com.datasiqn.arcadia.Arcadia;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class ArcadiaSender<T extends CommandSender> {
    private final Arcadia plugin;
    private final T base;

    public ArcadiaSender(Arcadia plugin, T base) {
        this.plugin = plugin;
        this.base = base;
    }

    public void sendMessage(String message) {
        plugin.sendMessage(message, base);
    }
    public void sendMessage(String prefix, String message) {
        sendMessageRaw(prefix + message);
    }

    public void sendMessageRaw(String message) {
        base.sendMessage(message);
    }

    public void sendDebugMessage(String message) {
        sendMessage(ChatColor.GREEN + "Debug > " + ChatColor.WHITE, message);
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
