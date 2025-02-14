package com.lucker.mobbuddies.buddies.utils;

import com.lucker.mobbuddies.MobBuddies;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class FollowOwnerGoal extends Goal {
    private final PathAwareEntity FollowerEntity;
    private LivingEntity owner;
    private final double speed;
    private final EntityNavigation navigation;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;

    public FollowOwnerGoal(PathAwareEntity FollowerEntity, double speed, float minDistance, float maxDistance) {
        MobBuddies.LOGGER.info("FollowOwnerGoal constructor");
        this.FollowerEntity = FollowerEntity;
        //MobBuddies.LOGGER.info(owner.toString());
        this.speed = speed;
        this.navigation = FollowerEntity.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP));
        if (!(FollowerEntity.getNavigation() instanceof MobNavigation) && !(FollowerEntity.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    private LivingEntity getOwner() {
        if (FollowerEntity instanceof IMobBuddyEntity buddy) {
            return buddy.getOwner();
        }
        return null;
    }

    @Override
    public boolean canStart() {
        LivingEntity owner = getOwner();
        return owner != null && FollowerEntity.squaredDistanceTo(owner) >= (double) (minDistance * minDistance);
    }

    @Override
    public boolean shouldContinue() {
        LivingEntity owner = getOwner();
        return owner != null && !navigation.isIdle() && FollowerEntity.squaredDistanceTo(owner) > (double) (maxDistance * maxDistance);
    }

    public void start() {
        //MobBuddies.LOGGER.info("Start");
        this.oldWaterPathfindingPenalty = this.FollowerEntity.getPathfindingPenalty(PathNodeType.WATER);
        this.FollowerEntity.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        this.FollowerEntity.setPathfindingPenalty(PathNodeType.OPEN, 0.0f);
        this.FollowerEntity.setPathfindingPenalty(PathNodeType.DANGER_OTHER, 0.0F); // Ignore "dangerous" paths
        this.FollowerEntity.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 0.0F); // Ignore damage areas
    }

    public void stop() {
        //MobBuddies.LOGGER.info("Stop");
        this.owner = null;
        this.navigation.stop();
        this.FollowerEntity.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
        this.FollowerEntity.setPathfindingPenalty(PathNodeType.OPEN, 0.0f);
        this.FollowerEntity.setPathfindingPenalty(PathNodeType.DANGER_OTHER, 0.0F); // Ignore "dangerous" paths
        this.FollowerEntity.setPathfindingPenalty(PathNodeType.DAMAGE_OTHER, 0.0F); // Ignore damage areas
    }

    @Override
    public void tick() {
        LivingEntity owner = getOwner();
        if (owner == null) {
            MobBuddies.LOGGER.warn("FollowOwnerGoal: Owner is null, stopping goal.");
            return; // Stop execution if the owner is null
        }

        FollowerEntity.getLookControl().lookAt(owner, 10.0F, FollowerEntity.getMaxLookPitchChange());

        if (FollowerEntity.squaredDistanceTo(owner) >= 144.0) {
            tryTeleportNear(owner.getBlockPos());
        } else {
            navigation.startMovingTo(owner, speed);
        }
    }

    private boolean shouldTryTeleportToOwner() {
        return owner != null && this.FollowerEntity.squaredDistanceTo(owner) >= 196.0;
    }

    private void tryTeleportNear(BlockPos pos) {
        for (int i = 0; i < 10; ++i) {
            int dx = this.FollowerEntity.getRandom().nextBetween(-3, 3);
            int dz = this.FollowerEntity.getRandom().nextBetween(-3, 3);
            int dy = this.FollowerEntity.getRandom().nextBetween(-1, 1);
            BlockPos targetPos = pos.add(dx, dy, dz);
            if (this.FollowerEntity.getWorld().isAir(targetPos) && this.FollowerEntity.getWorld().isAir(targetPos.up())) {
                this.FollowerEntity.refreshPositionAndAngles(targetPos.getX(), targetPos.getY(), targetPos.getZ(), this.FollowerEntity.getYaw(), this.FollowerEntity.getPitch());
                return;
            }
        }
    }
}