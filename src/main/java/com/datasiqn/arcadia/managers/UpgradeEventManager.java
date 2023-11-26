package com.datasiqn.arcadia.managers;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.upgrade.UpgradeType;
import com.datasiqn.arcadia.upgrade.actions.Action;
import com.datasiqn.arcadia.upgrade.listeners.ActionHandler;
import com.datasiqn.arcadia.upgrade.listeners.UpgradeListener;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
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
            if (method.getParameterCount() != 2) {
                plugin.getLogger().severe("The action handler " + method.getName() + "'s signature is incorrect! (in listener " + listener.getClass().getSimpleName() + ")");
                return;
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?> actionClass = parameterTypes[0];
            if (!Action.class.isAssignableFrom(actionClass) || !int.class.isAssignableFrom(parameterTypes[1])) {
                plugin.getLogger().severe("The action handler " + method.getName() + "'s signature is incorrect! (in listener " + listener.getClass().getSimpleName() + ")");
                return;
            }
            handlerList.add(actionClass.asSubclass(Action.class), new Handler<>(method.getAnnotation(ActionHandler.class).priority(), upgradeType, (action, stackAmount) -> {
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
            if (upgradeAmount == 0) continue;
            handler.onAction(action, upgradeAmount);
        }
    }

    public static final class HandlerList {
        private final ListMultimap<String, Handler<? extends Action>> handlers = ArrayListMultimap.create();

        public <T extends Action> @NotNull List<Handler<T>> getHandlers(@NotNull T action) {
            List<Handler<? extends Action>> handlerList = handlers.get(action.getClass().getSimpleName());
            List<Handler<T>> castedHandlers = new ArrayList<>(handlerList.size());
            //noinspection unchecked
            handlerList.forEach(handler -> castedHandlers.add((Handler<T>) handler));
            return castedHandlers;
        }

        public <T extends Action> boolean add(@NotNull Class<T> action, Handler<T> handler) {
            boolean putResult = handlers.put(action.getSimpleName(), handler);
            handlers.get(action.getSimpleName()).sort(Comparator.comparingInt(handler1 -> handler1.priority));
            return putResult;
        }

        public @NotNull Collection<Handler<? extends Action>> remove(@NotNull Action action) {
            return handlers.removeAll(action.getClass().getSimpleName());
        }
    }

    public static class Handler<T extends Action> {
        private final int priority;
        private final UpgradeType upgradeType;
        private final BiConsumer<T, Integer> onActionConsumer;

        public Handler(int priority, UpgradeType upgradeType, BiConsumer<T, Integer> onActionConsumer) {
            this.priority = priority;
            this.upgradeType = upgradeType;
            this.onActionConsumer = onActionConsumer;
        }

        public void onAction(T action, int stackAmount) {
            onActionConsumer.accept(action, stackAmount);
        }
    }
}
