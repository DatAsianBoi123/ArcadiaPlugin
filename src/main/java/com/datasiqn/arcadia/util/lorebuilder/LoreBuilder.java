package com.datasiqn.arcadia.util.lorebuilder;

import com.datasiqn.arcadia.util.lorebuilder.component.LoreComponent;
import com.datasiqn.arcadia.util.lorebuilder.component.TextLoreComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;

public class LoreBuilder {
    private final List<LoreComponent[]> lore = new ArrayList<>();

    public LoreBuilder() {}

    public LoreBuilder append(String text) {
        lore.add(new LoreComponent[] {TextLoreComponent.text(text)} );
        return this;
    }
    public LoreBuilder append(LoreComponent[] component) {
        lore.add(component);
        return this;
    }
    public LoreBuilder append(@NotNull Lore lore) {
        lore.asStringList().forEach(this::append);
        return this;
    }

    public LoreBuilder emptyLine() {
        lore.add(new LoreComponent[] {TextLoreComponent.text("")} );
        return this;
    }

    public Lore build() {
        List<String> lore = this.lore.stream().map(components -> {
            StringBuilder stringBuilder = new StringBuilder();
            for (LoreComponent component : components) {
                stringBuilder.append(component.toString());
            }
            return stringBuilder.toString();
        }).toList();

        return new BuilderLore(lore);
    }

    private record BuilderLore(List<String> stringList) implements Lore {
        @Override
        public @Unmodifiable List<String> asStringList() {
            return stringList;
        }
    }
}
