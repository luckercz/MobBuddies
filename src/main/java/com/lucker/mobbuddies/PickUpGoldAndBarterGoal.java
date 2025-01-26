package com.lucker.mobbuddies;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.entity.ItemEntity;

import java.util.List;
import java.util.Random;

public class PickUpGoldAndBarterGoal extends Goal {
    private final MobEntity mob;
    private ItemEntity targetGoldIngot;
    private final double speed;

    public PickUpGoldAndBarterGoal(MobEntity mob, double speed) {
        this.mob = mob;
        this.speed = speed;
    }

    @Override
    public boolean canStart() {
        // Always check for gold ingots within a 32-block radius
        this.targetGoldIngot = findNearestGoldIngot();
        return this.targetGoldIngot != null;
    }

    @Override
    public void start() {
        if (this.targetGoldIngot != null) {
            this.mob.getNavigation().startMovingTo(this.targetGoldIngot, this.speed);
        }
    }

    @Override
    public boolean shouldContinue() {
        // Ensure the target gold ingot is still in the world and valid
        if (this.targetGoldIngot == null || !this.targetGoldIngot.isAlive()) {
            this.targetGoldIngot = findNearestGoldIngot(); // Find another ingot
        }
        return true; // Always continue
    }

    @Override
    public void tick() {
        if (this.targetGoldIngot != null) {
            // If the ingot is removed, find another target
            if (!this.targetGoldIngot.isAlive()) {
                this.targetGoldIngot = findNearestGoldIngot();
                return;
            }

            // Move toward the current gold ingot
            this.mob.getNavigation().startMovingTo(this.targetGoldIngot, this.speed);

            // If close enough to pick it up, barter and find another gold ingot
            if (this.mob.squaredDistanceTo(this.targetGoldIngot) <= 1.5D) {
                pickUpGold();
                dropRandomItem();

                // Immediately find the next gold ingot after bartering
                this.targetGoldIngot = findNearestGoldIngot();

                // If no gold ingots are found, stop moving
                if (this.targetGoldIngot == null) {
                    this.mob.getNavigation().stop();
                }
            }
        } else {
            // If no target, search for another gold ingot
            this.targetGoldIngot = findNearestGoldIngot();

            // If no gold ingots are found, stop moving
            if (this.targetGoldIngot == null) {
                this.mob.getNavigation().stop();
            }
        }
    }

    private ItemEntity findNearestGoldIngot() {
        List<ItemEntity> nearbyItems = this.mob.getWorld().getEntitiesByClass(
                ItemEntity.class,
                this.mob.getBoundingBox().expand(32.0D),
                (item) -> item.getStack().getItem() == Items.GOLD_INGOT
        );
        return nearbyItems.isEmpty() ? null : nearbyItems.get(0);
    }

    private void pickUpGold() {
        if (this.targetGoldIngot != null && this.mob.getWorld() instanceof ServerWorld) {
            this.mob.playSound(SoundEvents.ENTITY_PIGLIN_ADMIRING_ITEM, 1.0F, 1.0F);
            this.targetGoldIngot.discard(); // Remove the gold ingot
        }
    }

    private void dropRandomItem() {
        if (this.mob.getWorld() instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) this.mob.getWorld();
            Random random = new Random();

            // Generate a random barter item
            ItemStack randomItem = getRandomBarterItem(random);

            // Drop the item at the mob's position
            ItemEntity itemEntity = new ItemEntity(
                    serverWorld,
                    this.mob.getX(),
                    this.mob.getY(),
                    this.mob.getZ(),
                    randomItem
            );
            serverWorld.spawnEntity(itemEntity);

            // Play a sound when dropping the item
            this.mob.playSound(SoundEvents.ENTITY_VILLAGER_TRADE, 1.0F, 1.0F);
        }
    }

    private ItemStack getRandomBarterItem(Random random) {
        ItemStack[] barterItems = new ItemStack[]{
                new ItemStack(Items.ENDER_PEARL, random.nextInt(2) + 1),
                new ItemStack(Items.IRON_NUGGET, random.nextInt(5) + 3),
                new ItemStack(Items.GLOWSTONE_DUST, random.nextInt(3) + 1),
                new ItemStack(Items.QUARTZ, random.nextInt(3) + 1),
                new ItemStack(Items.FIRE_CHARGE, 1),
                new ItemStack(Items.STRING, random.nextInt(6) + 1)
        };
        return barterItems[random.nextInt(barterItems.length)];
    }
}
