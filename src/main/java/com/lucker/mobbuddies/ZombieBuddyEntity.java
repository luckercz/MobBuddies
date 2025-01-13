package com.lucker.mobbuddies;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.entity.mob.PathAwareEntity;

import java.util.EnumSet;


public class ZombieBuddyEntity extends ZombieEntity {

    private PlayerEntity owner;

    public ZombieBuddyEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public static ZombieBuddyEntity create(World world, PlayerEntity owner, BlockPos pos) {
        MobBuddies.LOGGER.info("Createasdad zombie buddy");
        ZombieBuddyEntity zombieBuddy = new ZombieBuddyEntity(MobBuddies.ZOMBIE_BUDDY, world);
        zombieBuddy.refreshPositionAndAngles(pos, 0.0F, 0.0F);
        zombieBuddy.setOwner(owner);
        zombieBuddy.setCustomName(Text.of("zombuebud"));
        if (world instanceof ServerWorld) {
            ((ServerWorld) world).spawnEntity(zombieBuddy);
        }
        return zombieBuddy;
    }

    public void setOwner(PlayerEntity owner) {
        this.owner = owner;
    }

    public PlayerEntity getOwner() {
        return this.owner;
    }

    @Override
    protected void initGoals() {
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, net.minecraft.entity.mob.HostileEntity.class, true));

        this.goalSelector.add(1, new ZombieAttackGoal(this, 1.0D, true));
        this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0D, 5.0F, 30.0F)); // ADD FOLLOW
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        //this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0D));
    }

    @Override
    public boolean canTarget(EntityType<?> type) {
        return true;
        //return type != EntityType.PLAYER; // Only attack entities that are not players.
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (owner != null) {
            nbt.putUuid("OwnerUUID", owner.getUuid());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid("OwnerUUID") && getWorld() != null) {
            this.owner = getWorld().getPlayerByUuid(nbt.getUuid("OwnerUUID"));
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (player.getWorld().isClient) {
            return ActionResult.SUCCESS;
        }

        ItemStack heldItem = player.getStackInHand(hand);

        if (heldItem.isOf(ModItems.MOB_ENERGY_INGOT)) {
            if (this.getHealth() < this.getMaxHealth()) {
                this.heal(5.0F);
                heldItem.decrement(1);
                player.sendMessage(Text.literal("You healed your buddy!"), true);
            } else {
                player.sendMessage(Text.literal("Your buddy is already at full health!"), true);
            }
            return ActionResult.CONSUME;
        }
        else if (heldItem.isOf(Items.NAME_TAG)) {
            if (!heldItem.getName().getString().equals(Items.NAME_TAG.getName().getString())) {
                this.setCustomName(heldItem.getName());
                player.sendMessage(Text.literal("Your buddy is now named " + heldItem.getName().getString() + "!"), true);
                return ActionResult.CONSUME;
            }
        }

        return super.interactMob(player, hand);
    }

    @Override
    public boolean isCustomNameVisible() {
        return true; // Always render the name
    }
    @Override
    public boolean shouldRenderName() {
        return true;
    }
    @Override
    protected boolean burnsInDaylight(){
        return false;
    }
}
