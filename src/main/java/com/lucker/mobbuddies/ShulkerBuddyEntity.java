package com.lucker.mobbuddies;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import net.minecraft.entity.projectile.ShulkerBulletEntity;

import java.util.EnumSet;
import java.util.UUID;

public class ShulkerBuddyEntity extends ShulkerEntity implements IMobBuddyEntity {
    private UUID pendingOwnerUuid = null;
    private PlayerEntity owner;
    private int level = 1;
    private double customMaxHealth = 20.0; // Default max health
    public int customLevitationDuration = 200; // Default levitation duration
    public float customProjectileDamage = 4.0f;

    public ShulkerBuddyEntity(EntityType<? extends ShulkerEntity> entityType, World world) {
        super(entityType, world);
    }

    public static ShulkerBuddyEntity create(World world, PlayerEntity owner, BlockPos pos) {
        MobBuddies.LOGGER.info("Creating ShulkerBuddyEntity");

        ShulkerBuddyEntity shulkerBuddy = (ShulkerBuddyEntity) MobBuddyHelper.Create(world, new ShulkerBuddyEntity(MobBuddies.SHULKER_BUDDY, world), owner, pos);

        PlayerData playerData = StateSaverAndLoader.getPlayerState(owner);
        BuddyData buddyData = playerData.buddies.computeIfAbsent("shulker", k -> new BuddyData());

        MobBuddyHelper.Initialize(shulkerBuddy, buddyData.level, buddyData.health, buddyData.name);

        if (world instanceof ServerWorld) {
            ((ServerWorld) world).spawnEntity(shulkerBuddy);
        }
        return shulkerBuddy;
    }

    public void setOwner(PlayerEntity owner) {
        this.owner = owner;
    }

    public PlayerEntity getOwner() {
        return this.owner;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new ShulkerShootGoal(this)); // Shoot projectiles
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F)); // Look at players
        this.goalSelector.add(4, new LookAroundGoal(this)); // Look around randomly

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

        nbt.putInt("ShulkerBuddyLevel", this.level);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid("OwnerUUID")) {
            UUID ownerUuid = nbt.getUuid("OwnerUUID");
            this.setPendingOwner(ownerUuid);
        }

        if (nbt.contains("ShulkerBuddyLevel")) {
            this.levelUp(nbt.getInt("ShulkerBuddyLevel"));
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

        // Keep ShulkerBuddyEntity near the owner
        if (this.getOwner() != null && this.squaredDistanceTo(this.getOwner()) > 200.0) {
            teleportToOwner();
        }
    }

    private void teleportToOwner() {
        PlayerEntity owner = this.getOwner();
        if (owner != null) {
            Vec3d targetPos = owner.getPos().add((this.random.nextDouble() - 0.5) * 2.0, 0.5, (this.random.nextDouble() - 0.5) * 2.0);
            this.refreshPositionAndAngles(targetPos.x, targetPos.y, targetPos.z, this.getYaw(), this.getPitch());
        }
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
        this.customLevitationDuration += 10 * levels; // Increase levitation duration
        this.customProjectileDamage += 0.2 * levels;
        this.setCustomMaxHealth(this.getCustomMaxHealth() + 0.5 * levels); // Increase max health
        this.setLevel(this.getLevel() + levels);
    }

    public static DefaultAttributeContainer.Builder createCustomShulkerAttributes() {
        return ShulkerEntity.createShulkerAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0);
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
        playerData.buddies.get("shulker").name = name.getString();
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

    void setPeekAmount(int peekAmount) {
        if (!this.getWorld().isClient) {
            if (peekAmount == 0) {
                this.playSound(SoundEvents.ENTITY_SHULKER_CLOSE, 1.0F, 1.0F);
                this.emitGameEvent(GameEvent.CONTAINER_CLOSE);
            } else {
                this.playSound(SoundEvents.ENTITY_SHULKER_OPEN, 1.0F, 1.0F);
                this.emitGameEvent(GameEvent.CONTAINER_OPEN);
            }
        }
        this.dataTracker.set(PEEK_AMOUNT, (byte)peekAmount);
    }


    private static class ShulkerShootGoal extends Goal {
        private final ShulkerBuddyEntity shulker;
        private int cooldown;

        public ShulkerShootGoal(ShulkerBuddyEntity shulker) {
            this.shulker = shulker;
            this.setControls(EnumSet.of(Control.TARGET));
        }

        @Override
        public boolean canStart() {
            return this.shulker.getTarget() != null;
        }

        @Override
        public void start(){
            this.shulker.setPeekAmount(100);
        }

        @Override
        public void stop() {
            this.shulker.setPeekAmount(0);
        }

        @Override
        public void tick() {
            LivingEntity target = this.shulker.getTarget();
            if (target != null) {
                double distance = this.shulker.squaredDistanceTo(target);
                if (distance < 100.0 && this.shulker.canSee(target)) {
                    if (this.cooldown-- <= 0) {
                        this.shootProjectileAt(target);
                        this.cooldown = 20; // Cooldown between shots
                    }
                }
            }
        }

        private void shootProjectileAt(LivingEntity target) {
            Vec3d direction = target.getPos().subtract(this.shulker.getPos()).normalize();
            double x = direction.x;
            double y = direction.y;
            double z = direction.z;

            ShulkerBulletEntity bullet = new CustomShulkerBulletEntity(this.shulker.getWorld(), this.shulker, target, this.shulker.getAttachedFace().getAxis());
            bullet.setVelocity(x, y, z, 1.5F, 0.0F);
            this.shulker.getWorld().spawnEntity(bullet);

            this.shulker.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 1.0F, 1.0F);
        }
    }
}
