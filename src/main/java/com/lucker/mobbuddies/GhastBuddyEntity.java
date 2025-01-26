package com.lucker.mobbuddies;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

public class GhastBuddyEntity extends GhastEntity implements IMobBuddyEntity {
    private UUID pendingOwnerUuid = null;
    private PlayerEntity owner;
    private int level = 1;
    private double customAttackDamage = 1.0; // Default fireball damage
    private double customMaxHealth = 10.0; // Default max health

    public GhastBuddyEntity(EntityType<? extends GhastEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new GhastMoveControl(this);
    }

    public static GhastBuddyEntity create(World world, PlayerEntity owner, BlockPos pos) {
        MobBuddies.LOGGER.info("Creating GhastBuddyEntity");

        GhastBuddyEntity ghastBuddy = (GhastBuddyEntity) MobBuddyHelper.Create(world, new GhastBuddyEntity(MobBuddies.GHAST_BUDDY, world), owner, pos);

        PlayerData playerData = StateSaverAndLoader.getPlayerState(owner);
        BuddyData buddyData = playerData.buddies.computeIfAbsent("ghast", k -> new BuddyData());

        MobBuddyHelper.Initialize(ghastBuddy, buddyData.level, buddyData.health, buddyData.name);
        ghastBuddy.moveControl = new GhastMoveControl(ghastBuddy);
        if (world instanceof ServerWorld) {
            ((ServerWorld) world).spawnEntity(ghastBuddy);
        }
        return ghastBuddy;
    }

    public void setOwner(PlayerEntity owner) {
        this.owner = owner;
    }

    public PlayerEntity getOwner() {
        return this.owner;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new LookAtTargetGoal(this));
        this.goalSelector.add(3, new ShootFireballGoal(this)); // Fireball attack goal
        this.goalSelector.add(5, new FlyRandomlyGoal(this));

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, net.minecraft.entity.mob.HostileEntity.class, true)); // Target hostile mobs
    }

    @Override
    public boolean canTarget(EntityType<?> type) {
        return !MobBuddies.MOB_BUDDY_TYPES.contains(type); // Avoid targeting other buddies
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (owner != null) {
            nbt.putUuid("OwnerUUID", owner.getUuid());
        }

        nbt.putInt("GhastBuddyLevel", this.level);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid("OwnerUUID")) {
            UUID ownerUuid = nbt.getUuid("OwnerUUID");
            this.setPendingOwner(ownerUuid);
        }

        if (nbt.contains("GhastBuddyLevel")) {
            this.levelUp(nbt.getInt("GhastBuddyLevel"));
        }
    }

    public void setPendingOwner(UUID uuid) {
        this.pendingOwnerUuid = uuid;
    }

    public void resolvePendingOwner() {
        if (pendingOwnerUuid != null && getWorld() != null) {
            PlayerEntity resolvedOwner = getWorld().getPlayerByUuid(pendingOwnerUuid);
            if (resolvedOwner != null) {
                this.setOwner(resolvedOwner);
                this.pendingOwnerUuid = null; // Clear pending owner after resolving
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        resolvePendingOwner(); // Ensure the owner is set correctly
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (player.getWorld().isClient) {
            return ActionResult.SUCCESS;
        }
        PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
        ActionResult actionResult = MobBuddyHelper.InteractMob(player, this, hand, ModItems.MOB_ENERGY_INGOT, ModItems.MOB_ENERGY_RAW);
        if (actionResult == null) {
            return super.interactMob(player, hand);
        } else {
            return actionResult;
        }
    }

    public void levelUp(int levels) {
        this.customAttackDamage += 0.1 * levels; // Increase fireball damage
        this.setCustomMaxHealth(this.getCustomMaxHealth() + 2.0 * levels); // Increase max health
        this.setLevel(this.getLevel() + levels);
    }

    public static DefaultAttributeContainer.Builder createCustomGhastAttributes() {
        return GhastEntity.createGhastAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 100.0);
    }

    public double getCustomAttackDamage() {
        return customAttackDamage;
    }

    public void setCustomAttackDamage(double customAttackDamage) {
        this.customAttackDamage = customAttackDamage;
    }

    public double getCustomMaxHealth() {
        return customMaxHealth;
    }

    public void setCustomMaxHealth(double customMaxHealth) {
        double currentHealthPercentage = this.getHealth() / this.getMaxHealth();
        this.customMaxHealth = customMaxHealth;
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(customMaxHealth);
        this.setHealth((float) (customMaxHealth * currentHealthPercentage)); // Adjust current health proportionally
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public void setCustomName(@Nullable Text name) {
        PlayerData playerData = StateSaverAndLoader.getPlayerState(this.getOwner());
        playerData.buddies.get("ghast").name = name.getString();
        super.setCustomName(MobBuddyHelper.CustomName(name.getString(), this.getLevel()));
    }

    @Override
    public boolean isCustomNameVisible() {
        return true;
    }

    @Override
    public boolean shouldRenderName() {
        return true;
    }

    @Override
    protected boolean isDisallowedInPeaceful() {
        return false;
    }

    private static class FlyRandomlyGoal extends Goal {
        private final GhastBuddyEntity ghast;
        private final double speed = 1.0D;
        private final float maxDistance = 16;

        public FlyRandomlyGoal(GhastBuddyEntity ghast) {
            this.ghast = ghast;
            this.setControls(EnumSet.of(Control.MOVE));
        }

        @Override
        public boolean canStart() {
            PlayerEntity owner = this.ghast.getOwner();
            MoveControl moveControl = this.ghast.getMoveControl();
            double d = moveControl.getTargetX() - this.ghast.getX();
            double e = moveControl.getTargetY() - this.ghast.getY();
            double f = moveControl.getTargetZ() - this.ghast.getZ();
            double g = d * d + e * e + f * f;
            return g < 1.0 || g > maxDistance*maxDistance;

            //return owner != null && !this.ghast.getMoveControl().isMoving();
        }

        @Override
        public boolean shouldContinue() {
            return false; // The goal should run once per activation
        }

        @Override
        public void start() {
            Random random = this.ghast.getRandom();
            PlayerEntity owner = this.ghast.getOwner();

            if (owner != null) {
                double targetX;
                double targetY;
                double targetZ;
                targetX = owner.getX() + (random.nextDouble() - 0.5) * 6.0;
                targetY = owner.getY() + (random.nextDouble() + 5) * 2.0;
                targetZ = owner.getZ() + (random.nextDouble() - 0.5) * 6.0;

                this.ghast.getMoveControl().moveTo(targetX, targetY, targetZ, this.speed);
            }
        }
    }


    private static class ShootFireballGoal extends Goal {
        private final GhastBuddyEntity ghast;
        public int cooldown;

        public ShootFireballGoal(GhastBuddyEntity ghast) {
            this.ghast = ghast;
        }

        public boolean canStart() {
            return this.ghast.getTarget() != null;
        }

        public void start() {
            this.cooldown = 0;
        }

        public void stop() {
            this.ghast.setShooting(false);
        }

        public boolean shouldRunEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingEntity = this.ghast.getTarget();
            if (livingEntity != null) {
                double d = 64.0;
                if (livingEntity.squaredDistanceTo(this.ghast) < 4096.0 && this.ghast.canSee(livingEntity)) {
                    World world = this.ghast.getWorld();
                    ++this.cooldown;
                    if (this.cooldown == 10 && !this.ghast.isSilent()) {
                        world.syncWorldEvent((PlayerEntity)null, 1015, this.ghast.getBlockPos(), 0);
                    }

                    if (this.cooldown == 20) {
                        double e = 4.0;
                        Vec3d vec3d = this.ghast.getRotationVec(1.0F);
                        double f = livingEntity.getX() - (this.ghast.getX() + vec3d.x * 4.0);
                        double g = livingEntity.getBodyY(0.5) - (0.5 + this.ghast.getBodyY(0.5));
                        double h = livingEntity.getZ() - (this.ghast.getZ() + vec3d.z * 4.0);
                        Vec3d vec3d2 = new Vec3d(f, g, h);
                        if (!this.ghast.isSilent()) {
                            world.syncWorldEvent((PlayerEntity)null, 1016, this.ghast.getBlockPos(), 0);
                        }

                        FireballEntity fireballEntity = new FireballEntity(world, this.ghast, vec3d2.normalize(), (int)Math.floor(this.ghast.customAttackDamage));
                        fireballEntity.setPosition(this.ghast.getX() + vec3d.x * 4.0, this.ghast.getBodyY(0.5) + 0.5, fireballEntity.getZ() + vec3d.z * 4.0);
                        world.spawnEntity(fireballEntity);
                        this.cooldown = -40;
                    }
                } else if (this.cooldown > 0) {
                    --this.cooldown;
                }

                this.ghast.setShooting(this.cooldown > 10);
            }
        }
    }

    static class LookAtTargetGoal extends Goal {
        private final GhastEntity ghast;

        public LookAtTargetGoal(GhastEntity ghast) {
            this.ghast = ghast;
            this.setControls(EnumSet.of(Control.LOOK));
        }

        public boolean canStart() {
            return true;
        }

        public boolean shouldRunEveryTick() {
            return true;
        }

        public void tick() {
            if (this.ghast.getTarget() == null) {
                Vec3d vec3d = this.ghast.getVelocity();
                this.ghast.setYaw(-((float)MathHelper.atan2(vec3d.x, vec3d.z)) * 57.295776F);
                this.ghast.bodyYaw = this.ghast.getYaw();
            } else {
                LivingEntity livingEntity = this.ghast.getTarget();
                double d = 64.0;
                if (livingEntity.squaredDistanceTo(this.ghast) < 4096.0) {
                    double e = livingEntity.getX() - this.ghast.getX();
                    double f = livingEntity.getZ() - this.ghast.getZ();
                    this.ghast.setYaw(-((float)MathHelper.atan2(e, f)) * 57.295776F);
                    this.ghast.bodyYaw = this.ghast.getYaw();
                }
            }

        }
    }

    static class GhastMoveControl extends MoveControl {
        private final GhastEntity ghast;
        private int collisionCheckCooldown;

        public GhastMoveControl(GhastEntity ghast) {
            super(ghast);
            this.ghast = ghast;
        }

        public void tick() {
            if (this.state == State.MOVE_TO) {
                if (this.collisionCheckCooldown-- <= 0) {
                    this.collisionCheckCooldown += this.ghast.getRandom().nextInt(5) + 2;
                    Vec3d vec3d = new Vec3d(this.targetX - this.ghast.getX(), this.targetY - this.ghast.getY(), this.targetZ - this.ghast.getZ());
                    double d = vec3d.length();
                    vec3d = vec3d.normalize();
                    if (this.willCollide(vec3d, MathHelper.ceil(d))) {
                        this.ghast.setVelocity(this.ghast.getVelocity().add(vec3d.multiply(0.1)));
                    } else {
                        this.state = State.WAIT;
                    }
                }

            }
        }

        private boolean willCollide(Vec3d direction, int steps) {
            Box box = this.ghast.getBoundingBox();

            for(int i = 1; i < steps; ++i) {
                box = box.offset(direction);
                if (!this.ghast.getWorld().isSpaceEmpty(this.ghast, box)) {
                    return false;
                }
            }

            return true;
        }
    }
}
