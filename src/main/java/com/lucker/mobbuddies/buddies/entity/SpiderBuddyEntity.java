package com.lucker.mobbuddies.buddies.entity;

import com.lucker.mobbuddies.*;
import com.lucker.mobbuddies.buddies.utils.FollowOwnerGoal;
import com.lucker.mobbuddies.buddies.utils.IMobBuddyEntity;
import com.lucker.mobbuddies.buddies.utils.MobBuddyHelper;
import com.lucker.mobbuddies.utils.BuddyData;
import com.lucker.mobbuddies.utils.PlayerData;
import com.lucker.mobbuddies.utils.StateSaverAndLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SpiderBuddyEntity extends SpiderEntity implements IMobBuddyEntity {

    private UUID pendingOwnerUuid = null;
    private PlayerEntity owner;
    private int level = 1;
    private double customAttackDamage = 5.0; // Default attack damage
    private double customSpeed = 0.25; // Default movement speed
    private double customMaxHealth = 20.0; // Default max health

    public SpiderBuddyEntity(EntityType<? extends SpiderEntity> entityType, World world) {
        super(entityType, world);
    }

    public static SpiderBuddyEntity create(World world, PlayerEntity owner, BlockPos pos) {
        MobBuddies.LOGGER.info("Creating SpiderBuddyEntity");

        SpiderBuddyEntity spiderBuddy = (SpiderBuddyEntity) MobBuddyHelper.Create(world, new SpiderBuddyEntity(MobBuddies.SPIDER_BUDDY, world), owner, pos);

        PlayerData playerData = StateSaverAndLoader.getPlayerState(owner);
        BuddyData buddyData = playerData.buddies.computeIfAbsent("spider", k -> new BuddyData());

        MobBuddyHelper.Initialize(spiderBuddy, buddyData.level, buddyData.health, buddyData.name);

        if (world instanceof ServerWorld) {
            ((ServerWorld) world).spawnEntity(spiderBuddy);
        }
        return spiderBuddy;
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

        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true)); // Melee attack
        this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0D, 7.0F, 30.0F)); // Follow owner
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0D)); // Wander
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F)); // Look at player
        this.goalSelector.add(5, new LookAroundGoal(this)); // Randomly look around
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

        nbt.putInt("SpiderBuddyLevel", this.level);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid("OwnerUUID")) {
            UUID ownerUuid = nbt.getUuid("OwnerUUID");
            this.setPendingOwner(ownerUuid);
        }

        if (nbt.contains("SpiderBuddyLevel")) {
            this.levelUp(nbt.getInt("SpiderBuddyLevel"));
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
        resolvePendingOwner(); // Attempt to resolve the owner each tick
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (player.getWorld().isClient) {
            return ActionResult.SUCCESS;
        }
        PlayerData playerData = StateSaverAndLoader.getPlayerState(player);
        ActionResult actionResult = MobBuddyHelper.InteractMob(player, this, hand, ModItems.SPIDER_HEAL, ModItems.SPIDER_UPGRADE);
        if (actionResult == null) {
            return super.interactMob(player, hand);
        } else {
            return actionResult;
        }
    }

    public void levelUp(int levels) {
        this.setCustomAttackDamage(this.getCustomAttackDamage() + 1.0 * levels); // Increase attack damage
        this.setCustomSpeed(this.getCustomSpeed() + 0.015 * levels); // Increase speed
        this.setCustomMaxHealth(this.getCustomMaxHealth() + 1.0 * levels); // Increase max health by 1 per level
        this.setLevel(this.getLevel() + levels);
    }

    public static DefaultAttributeContainer.Builder createCustomSpiderAttributes() {
        return SpiderEntity.createSpiderAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0);
    }

    public double getCustomAttackDamage() {
        return customAttackDamage;
    }

    public void setCustomAttackDamage(double customAttackDamage) {
        this.customAttackDamage = customAttackDamage;
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(customAttackDamage);
    }

    public double getCustomSpeed() {
        return customSpeed;
    }

    public void setCustomSpeed(double customSpeed) {
        this.customSpeed = customSpeed;
        this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(customSpeed);
    }

    public double getCustomMaxHealth() {
        return customMaxHealth;
    }

    public void setCustomMaxHealth(double customMaxHealth) {
        double currentHealthPercentage = this.getHealth() / this.getMaxHealth();
        this.customMaxHealth = customMaxHealth;
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(customMaxHealth);
        this.setHealth((float) (customMaxHealth * currentHealthPercentage)); // Adjust health proportionally
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
        playerData.buddies.get("spider").name = name.getString();
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
}
