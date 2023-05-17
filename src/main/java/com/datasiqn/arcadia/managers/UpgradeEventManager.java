package com.datasiqn.arcadia.managers;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.upgrades.UpgradeType;
import com.datasiqn.arcadia.upgrades.listeners.ActionHandler;
import com.datasiqn.arcadia.upgrades.listeners.UpgradeListener;
import com.datasiqn.arcadia.upgrades.listeners.actions.Action;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class UpgradeEventManager {
    private final HandlerList handlerList = new HandlerList();
    private final Arcadia plugin;

    public UpgradeEventManager(@NotNull Arcadia plugin) {
        this.plugin = plugin;
    }

    public void register(@NotNull UpgradeListener listener, UpgradeType upgradeType) {
        for (Method method : listener.getClass().getMethods()) {
            if (!method.isAnnotationPresent(ActionHandler.class)) return;
            if (method.getParameterCount() != 2) return;
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?> actionClass = parameterTypes[0];
            if (!Action.class.isAssignableFrom(actionClass) || !int.class.isAssignableFrom(parameterTypes[1])) return;
            handlerList.add(actionClass.asSubclass(Action.class), new Handler<>(upgradeType, (action, stackAmount) -> {
                try {
                    method.invoke(listener, action, stackAmount);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }));
            plugin.getLogger().info("Registered action handler " + actionClass.getSimpleName());
        }
    }

    public void emit(@NotNull Action action) {
        for (Handler<Action> handler : handlerList.getHandlers(action)) {
            int upgradeAmount = action.getPlayer().getUpgradeAmount(handler.upgradeType);
            if (upgradeAmount == 0) break;
            handler.onAction(action, upgradeAmount);
        }
    }

    public static final class HandlerList {
        private final Multimap<String, Handler<? extends Action>> handlers = ArrayListMultimap.create();

        public <T extends Action> @NotNull Collection<Handler<T>> getHandlers(@NotNull T action) {
            Collection<Handler<? extends Action>> handlerCollection = handlers.get(action.getClass().getSimpleName());
            Set<Handler<T>> castedHandlers = new HashSet<>(handlerCollection.size());
            //noinspection unchecked
            handlerCollection.forEach(handler -> castedHandlers.add((Handler<T>) handler));
            return castedHandlers;
        }

        public <T extends Action> boolean add(@NotNull Class<T> action, Handler<T> handler) {
            return handlers.put(action.getSimpleName(), handler);
        }

        public @NotNull Collection<Handler<? extends Action>> remove(@NotNull Action action) {
            return handlers.removeAll(action.getClass().getSimpleName());
        }
    }

    public static class Handler<T extends Action> {
        private final UpgradeType upgradeType;
        private final BiConsumer<T, Integer> onActionConsumer;

        public Handler(UpgradeType upgradeType, BiConsumer<T, Integer> onActionConsumer) {
            this.upgradeType = upgradeType;
            this.onActionConsumer = onActionConsumer;
        }

        public void onAction(T action, int stackAmount) {
            onActionConsumer.accept(action, stackAmount);
        }
    }
}
