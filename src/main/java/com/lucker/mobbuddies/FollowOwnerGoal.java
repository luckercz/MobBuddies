package com.lucker.mobbuddies;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class FollowOwnerGoal extends Goal {
    private final PathAwareEntity FollowerEntity;
    private LivingEntity owner;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private final float minDistance;
    private float oldWaterPathfindingPenalty;

    public FollowOwnerGoal(PathAwareEntity FollowerEntity, LivingEntity owner, double speed, float minDistance, float maxDistance) {
        MobBuddies.LOGGER.info("Consturcotr");
        this.FollowerEntity = FollowerEntity;
        //MobBuddies.LOGGER.info(owner.toString());
        this.owner = owner;
        this.speed = speed;
        this.navigation = FollowerEntity.getNavigation();
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        if (!(FollowerEntity.getNavigation() instanceof MobNavigation) && !(FollowerEntity.getNavigation() instanceof BirdNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    public boolean canStart() {
        LivingEntity livingEntity = owner;
        if (livingEntity == null) {
            return false;
        } //else if (this.FollowerEntity.cannotFollowOwner()) {
            //return false;
        //}
        else if (this.FollowerEntity.squaredDistanceTo(livingEntity) < (double)(this.minDistance * this.minDistance)) {
            return false;
        } else {
            this.owner = livingEntity;
            return true;
        }
    }

    public boolean shouldContinue() {
        if (this.navigation.isIdle()) {
            return false;
        } //else if (this.FollowerEntity.cannotFollowOwner()) {
            //return false;
        //}
        else {
            return !(this.FollowerEntity.squaredDistanceTo(this.owner) <= (double)(this.maxDistance * this.maxDistance));
        }
    }

    public void start() {
        MobBuddies.LOGGER.info("Start");
        this.updateCountdownTicks = 0;
        this.oldWaterPathfindingPenalty = this.FollowerEntity.getPathfindingPenalty(PathNodeType.WATER);
        this.FollowerEntity.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
    }

    public void stop() {
        MobBuddies.LOGGER.info("Stop");
        this.owner = null;
        this.navigation.stop();
        this.FollowerEntity.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
    }

    public void tick() {
        MobBuddies.LOGGER.info("Ticking FollowOwnerGoal");
        boolean bl = false; //shouldTryTeleportToOwner();
        if (!bl) {
            this.FollowerEntity.getLookControl().lookAt(this.owner, 10.0F, (float)this.FollowerEntity.getMaxLookPitchChange());
        }

        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = this.getTickCount(10);
            if (bl) {
                //this.FollowerEntity.tryTeleportToOwner();
            } else {
                this.navigation.startMovingTo(this.owner, this.speed);
            }

        }
    }
//    public boolean shouldTryTeleportToOwner() {
//        return owner != null && this.FollowerEntity.squaredDistanceTo(owner) >= 144.0;
//    }
//    private void tryTeleportNear(BlockPos pos) {
//        for(int i = 0; i < 10; ++i) {
//            int j = this.random.nextBetween(-3, 3);
//            int k = this.random.nextBetween(-3, 3);
//            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
//                int l = this.random.nextBetween(-1, 1);
//                if (this.tryTeleportTo(pos.getX() + j, pos.getY() + l, pos.getZ() + k)) {
//                    return;
//                }
//            }
//        }
//
//    }
}