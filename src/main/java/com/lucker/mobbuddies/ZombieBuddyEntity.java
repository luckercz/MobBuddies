package com.lucker.mobbuddies;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
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
        MobBuddies.LOGGER.info("Create zombie buddy");
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
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, net.minecraft.entity.mob.HostileEntity.class, true));

        this.goalSelector.add(1, new ZombieAttackGoal(this, 1.0D, true));
        //this.goalSelector.add(1, new FollowOwnerGoal(this, owner, 1.0D, 3.0F, 15.0F)); // ADD FOLLOW
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0D));
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
    public boolean shouldRenderName() {
        return true;
    }

    @Override
    protected boolean burnsInDaylight(){
        return false;
    }
}
