package com.lucker.mobbuddies;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ZombieBuddyEntity extends ZombieEntity {

    private PlayerEntity owner;

    public ZombieBuddyEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public static ZombieBuddyEntity create(World world, PlayerEntity owner, BlockPos pos) {
        ZombieBuddyEntity zombieBuddy = new ZombieBuddyEntity(EntityType.ZOMBIE, world);
        zombieBuddy.refreshPositionAndAngles(pos, 0.0F, 0.0F);
        if (world instanceof ServerWorld) {
            ((ServerWorld) world).spawnEntity(zombieBuddy);
        }
        zombieBuddy.setOwner(owner);
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
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true));
        //this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0D));
    }

    @Override
    public boolean canTarget(EntityType<?> type) {
        return super.canTarget(type) && type != EntityType.PLAYER; // Only attack entities that are not players.
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
    public boolean shouldRenderName() {
        return true;
    }

}
