package com.datasiqn.arcadia.items.abilities;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.util.lorebuilder.Lore;
import com.datasiqn.schedulebuilder.ScheduleBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class MarkAbility extends ItemAbility {
    public MarkAbility() {
        super("Mark", Lore.of("Throws your sword out, marking the enemy it hits.", "Hitting a marked enemy with this sword will deal double damage."), 40);
    }

    @Override
    public void execute(@NotNull AbilityExecutor executor) {
        Player player = executor.playerData().getPlayer();
        Location location = player.getLocation();
        location.subtract(0, 0.3, 0);
        Vector playerDirection = location.getDirection();
        playerDirection.setY(0);
        playerDirection.normalize();
        location.add(0.54 * playerDirection.getZ(), 0, -0.54 * playerDirection.getX());
        ArmorStand armorStand = player.getWorld().spawn(location, ArmorStand.class, stand -> {
            stand.setGravity(false);
            stand.setHeadPose(new EulerAngle(Math.PI / 2, Math.PI / 4, 0));
            EntityEquipment equipment = stand.getEquipment();
            if (equipment == null) return;
            equipment.setHelmet(new ItemStack(Material.NETHERITE_SWORD));
        });

        ScheduleBuilder.create()
                .repeatEvery(1).ticks()
                .executes(runnable -> {
                    Location newLoc = armorStand.getLocation();
                    Vector direction = newLoc.getDirection();
                    newLoc.add(direction.clone().multiply(1));
                    armorStand.teleport(newLoc);

                    Location particleLoc = armorStand.getLocation();
                    direction.setY(0);
                    direction.normalize();
                    particleLoc.add(-0.5 * direction.getZ(), 1.7, 0.5 * direction.getX());
                    particleLoc.add(direction.clone().multiply(0.85));

                    World world = armorStand.getWorld();
                    world.spawnParticle(Particle.REDSTONE, particleLoc, 2, new Particle.DustOptions(Color.RED, 1));

                    Block block = world.getBlockAt(particleLoc);
                    if (block.isLiquid() || block.isPassable()) return;
                    if (block.getCollisionShape().overlaps(BoundingBox.of(particleLoc.subtract(block.getLocation()), 0.1, 0.1, 0.1))) {
                        ScheduleBuilder.create()
                                .wait(1d).seconds()
                                .executes(r -> armorStand.remove())
                                .run(JavaPlugin.getPlugin(Arcadia.class));

                        runnable.cancel();
                    }
                }).run(JavaPlugin.getProvidingPlugin(Arcadia.class));
    }
}
