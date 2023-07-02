package com.datasiqn.arcadia.item.abilities;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.util.lorebuilder.Lore;
import com.datasiqn.schedulebuilder.ScheduleBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4d;

public class MarkAbility extends ItemAbility {
    public MarkAbility() {
        super("Mark", Lore.of("Throws your sword out, marking the enemy it hits.", "Hitting a marked enemy with this sword will deal double damage."), 40);
    }

    @Override
    public void execute(@NotNull AbilityExecutor executor) {
        Player player = executor.playerData().getPlayer();
        Location location = player.getEyeLocation();
        Vector direction = location.getDirection();
        ItemDisplay itemDisplay = player.getWorld().spawn(location, ItemDisplay.class, display -> {
            display.setItemStack(new ItemStack(Material.NETHERITE_SWORD));
            display.setBrightness(new Display.Brightness(15, 15));
            Transformation transformation = display.getTransformation();
            transformation.getScale().set(0.75);
            transformation.getLeftRotation().set(new AxisAngle4d(-(Math.PI * 0.75) - Math.toRadians(location.getYaw()), 0, 1, 0));
            transformation.getRightRotation().set(new AxisAngle4d(-Math.PI / 2, 1, 0, 0));
            display.setTransformation(transformation);
        });

        ScheduleBuilder.create()
                .repeatEvery(1).ticks()
                .executes(runnable -> {
                    Location newLoc = itemDisplay.getLocation();
                    newLoc.add(direction);
                    itemDisplay.teleport(newLoc);

                    Location particleLoc = itemDisplay.getLocation();
                    Vector particleDirection = direction.clone();
                    particleDirection.setY(0);
                    particleDirection.normalize();
                    particleLoc.add(particleDirection.multiply(0.68));

                    World world = itemDisplay.getWorld();
                    world.spawnParticle(Particle.REDSTONE, particleLoc, 2, new Particle.DustOptions(Color.RED, 1));

                    Block block = world.getBlockAt(particleLoc);
                    if (block.isLiquid() || block.isPassable()) return;
                    if (block.getCollisionShape().overlaps(BoundingBox.of(particleLoc.subtract(block.getLocation()), 0.1, 0.1, 0.1))) {
                        ScheduleBuilder.create()
                                .wait(1d).seconds()
                                .executes(r -> itemDisplay.remove())
                                .run(JavaPlugin.getPlugin(Arcadia.class));

                        runnable.cancel();
                    }
                }).run(JavaPlugin.getProvidingPlugin(Arcadia.class));
    }
}
