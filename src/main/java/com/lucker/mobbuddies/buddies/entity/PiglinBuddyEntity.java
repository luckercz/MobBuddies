package com.lucker.mobbuddies.buddies.entity;

import com.lucker.mobbuddies.*;
import com.lucker.mobbuddies.buddies.utils.*;
import com.lucker.mobbuddies.buddies.utils.FollowOwnerGoal;
import com.lucker.mobbuddies.utils.BuddyData;
import com.lucker.mobbuddies.utils.PlayerData;
import com.lucker.mobbuddies.utils.StateSaverAndLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class PiglinBuddyEntity extends ZombieEntity implements IMobBuddyEntity {
    private UUID pendingOwnerUuid = null;
    private PlayerEntity owner;
    private int level = 1;
    private double customAttackDamage = 5.0; // Default attack damage
    private double customMaxHealth = 20.0; // Default max health

    public PiglinBuddyEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public static PiglinBuddyEntity create(World world, PlayerEntity owner, BlockPos pos) {
        MobBuddies.LOGGER.info("Creating PiglinBuddyEntity");

        PiglinBuddyEntity piglinBuddy = (PiglinBuddyEntity) MobBuddyHelper.Create(world, new PiglinBuddyEntity(MobBuddies.PIGLIN_BUDDY, world), owner, pos);

        PlayerData playerData = StateSaverAndLoader.getPlayerState(owner);
        BuddyData buddyData = playerData.buddies.computeIfAbsent("piglin", k -> new BuddyData());

        MobBuddyHelper.Initialize(piglinBuddy, buddyData.level, buddyData.health, buddyData.name);

        piglinBuddy.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));

        if (world instanceof ServerWorld) {
            ((ServerWorld) world).spawnEntity(piglinBuddy);
        }
        return piglinBuddy;
    }

    public void setOwner(PlayerEntity owner) {
        this.owner = owner;
    }

    public PlayerEntity getOwner() {
        return this.owner;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true)); // Melee attack
        this.goalSelector.add(2, new PickUpGoldAndBarterGoal(this, 1.0D));
        this.goalSelector.add(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 40.0F)); // Follow owner
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F)); // Look at players
        this.goalSelector.add(6, new LookAroundGoal(this)); // Random idle looking

        this.targetSelector.add(1, new ActiveTargetGoal<>(this, HostileEntity.class, true)); // Target hostile mobs
        this.targetSelector.add(2, new RevengeGoal(this)); // Target entities that attacked it
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

        nbt.putInt("PiglinBuddyLevel", this.level);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid("OwnerUUID")) {
            UUID ownerUuid = nbt.getUuid("OwnerUUID");
            this.setPendingOwner(ownerUuid);
        }

        if (nbt.contains("PiglinBuddyLevel")) {
            this.levelUp(nbt.getInt("PiglinBuddyLevel"));
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
        ActionResult actionResult = MobBuddyHelper.InteractMob(player, this, hand, ModItems.GOLDEN_RATION, ModItems.GILDED_TREAT);
        if (actionResult == null) {
            return super.interactMob(player, hand);
        } else {
            return actionResult;
        }
    }

    public void levelUp(int levels) {
        this.setCustomAttackDamage(this.getCustomAttackDamage() + 1.0 * levels); // Increase attack damage
        this.setCustomMaxHealth(this.getCustomMaxHealth() + 2.0 * levels); // Increase max health
        this.setLevel(this.getLevel() + levels);
    }

    public static DefaultAttributeContainer.Builder createCustomPiglinAttributes() {
        return PiglinEntity.createPiglinAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0);
    }

    public double getCustomAttackDamage() {
        return customAttackDamage;
    }

    public void setCustomAttackDamage(double customAttackDamage) {
        this.customAttackDamage = customAttackDamage;
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(customAttackDamage);
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
        playerData.buddies.get("piglin").name = name.getString();
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
    protected boolean burnsInDaylight() {
        return false;
    }

    @Override
    protected boolean isDisallowedInPeaceful() {
        return false;
    }

    @Override
    protected boolean canConvertInWater() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PIGLIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_PIGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIGLIN_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_PIGLIN_DEATH;
    }
}
