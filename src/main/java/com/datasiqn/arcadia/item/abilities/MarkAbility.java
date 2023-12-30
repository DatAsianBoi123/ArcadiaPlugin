package com.datasiqn.arcadia.item.abilities;

import com.datasiqn.arcadia.Arcadia;
import com.datasiqn.arcadia.entities.ArcadiaEntity;
import com.datasiqn.arcadia.util.lorebuilder.Lore;
import com.datasiqn.schedulebuilder.ScheduleBuilder;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4d;

public class MarkAbility extends ItemAbility {
    private final Arcadia plugin;

    public MarkAbility(Arcadia plugin) {
        super("Mark", Lore.of("Throws your sword out, marking the enemy it hits.", "Hitting a marked enemy with this sword will deal double damage."), 40);
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull AbilityExecuteContext executor) {
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

        long startTime = System.currentTimeMillis();

        ScheduleBuilder.create()
                .repeatEvery(1).ticks()
                .executes(runnable -> {
                    if (System.currentTimeMillis() - startTime > 5000) {
                        runnable.cancel();
                        itemDisplay.remove();
                        return;
                    }

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

                    if (checkBlockCollision(world, particleLoc)) {
                        runnable.cancel();
                        ScheduleBuilder.create()
                                .wait(1d).seconds()
                                .executes(r -> itemDisplay.remove())
                                .run(plugin);
                        return;
                    }

                    ArcadiaEntity entityCollision = checkEntityCollision(world, particleLoc);
                    if (entityCollision != null) {
                        CraftEntity bukkitEntity = entityCollision.getBukkitEntity();
                        runnable.cancel();
                        itemDisplay.remove();

                        entityCollision.mark("mark-ability:marked");

                        ((CraftPlayer) player).getHandle().connection.send(new ClientboundHurtAnimationPacket(entityCollision.getId(), 0));
                        long beginBleedTime = System.currentTimeMillis();
                        ScheduleBuilder.create()
                                .repeatEvery(1d).ticks()
                                .executes(r -> {
                                    if (bukkitEntity.isDead() || System.currentTimeMillis() - beginBleedTime > 10_000) {
                                        r.cancel();
                                        entityCollision.unmark("mark-ability:marked");
                                        return;
                                    }
                                    Location bleedParticleLoc = bukkitEntity.getLocation().add(0, 1, 0);
                                    world.spawnParticle(Particle.BLOCK_DUST, bleedParticleLoc, 10, 0, 0.5, 0, 1, Bukkit.createBlockData(Material.REDSTONE_BLOCK));
                                }).run(plugin);
                    }
                }).run(plugin);
    }

    private @Nullable ArcadiaEntity checkEntityCollision(@NotNull World world, Location location) {
        for (Entity entity : world.getChunkAt(location).getEntities()) {
            if (!(entity instanceof LivingEntity)) continue;
            if (!(((CraftEntity) entity).getHandle() instanceof ArcadiaEntity arcadiaEntity)) continue;
            if (!entity.getBoundingBox().overlaps(BoundingBox.of(location, 0.1, 0.1, 0.1))) continue;
            return arcadiaEntity;
        }
        return null;
    }

    private boolean checkBlockCollision(@NotNull World world, Location location) {
        Block block = world.getBlockAt(location);
        if (block.isLiquid() || block.isPassable()) return false;
        return block.getCollisionShape().overlaps(BoundingBox.of(location.clone().subtract(block.getLocation()), 0.1, 0.1, 0.1));
    }
}
