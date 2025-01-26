package com.lucker.mobbuddies;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;


public class SkeletonBuddyEntity extends SkeletonEntity implements IMobBuddyEntity {

    private UUID pendingOwnerUuid = null;
    private PlayerEntity owner;
    private int level = 1;
    private float customArrowDamageModifier = 5.0f; // Default attack damage
    private double customMaxHealth = 20.0; // Default health

    public SkeletonBuddyEntity(EntityType<? extends SkeletonEntity> entityType, World world) {
        super(entityType, world);
    }

    public static SkeletonBuddyEntity create(World world, PlayerEntity owner, BlockPos pos) {
        MobBuddies.LOGGER.info("Createasdad skleton buddy");

        SkeletonBuddyEntity skeletonBuddy = (SkeletonBuddyEntity) MobBuddyHelper.Create(world, new SkeletonBuddyEntity(MobBuddies.SKELETON_BUDDY, world), owner, pos);

        // 1 instance
        PlayerData playerData = StateSaverAndLoader.getPlayerState(owner);
        BuddyData buddyData = playerData.buddies.computeIfAbsent("skeleton", k -> new BuddyData());

        MobBuddyHelper.Initialize(skeletonBuddy, buddyData.level, buddyData.health, buddyData.name);

//        skeletonBuddy.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.STONE_BUTTON));
//        skeletonBuddy.setEquipmentDropChance(EquipmentSlot.HEAD, 0.0f);

        skeletonBuddy.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));

        //If unlocked

        if (world instanceof ServerWorld) {
            ((ServerWorld) world).spawnEntity(skeletonBuddy);
        }
        return skeletonBuddy;
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

        this.goalSelector.add(1, new BowAttackGoal<>(this, 1.0D, 20, 15.0f));
        this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0D, 5.0F, 30.0F)); // ADD FOLLOW
        this.goalSelector.add(3, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        //this.goalSelector.add(4, new WanderAroundFarGoal(this, 1.0D));
    }

    @Override
    protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier, @Nullable ItemStack shotFrom) {
        return ProjectileUtil.createArrowProjectile(this, arrow, damageModifier + this.getCustomArrowDamageModifier(), shotFrom);
    }

    @Override
    public boolean canTarget(EntityType<?> type) {
        return !MobBuddies.MOB_BUDDY_TYPES.contains(type);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (owner != null) {
            nbt.putUuid("OwnerUUID", owner.getUuid());
        }

        nbt.putInt("SkeletonBuddyLevel", this.level);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid("OwnerUUID")) {
            UUID ownerUuid = nbt.getUuid("OwnerUUID");
            this.setPendingOwner(ownerUuid);
        }

        if(nbt.contains("SkeletonBuddyLevel")) {
            this.levelUp(nbt.getInt("SkeletonBuddyLevel"));
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
        ActionResult actionResult = MobBuddyHelper.InteractMob(player, this, hand, ModItems.MOB_ENERGY_INGOT, ModItems.MOB_ENERGY_RAW);
        if(actionResult == null){
            return super.interactMob(player, hand);
        }
        else{
            return actionResult;
        }
    }

    public void levelUp(int levels){
        this.setCustomArrowDamageModifier(this.getCustomArrowDamageModifier()+ 1.0f * levels);
        this.setCustomMaxHealth(this.getCustomMaxHealth() + 2.0f * levels);
        this.setLevel(this.getLevel() + levels);
    }

    public static DefaultAttributeContainer.Builder createCustomSkeletonAttributes() {
        return SkeletonBuddyEntity.createAbstractSkeletonAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 100.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0);
    }

    public float getCustomArrowDamageModifier(){
        return customArrowDamageModifier;
    }

    public void setCustomArrowDamageModifier(float customArrowDamageModifier){
        this.customArrowDamageModifier = customArrowDamageModifier;
    }

    public double getCustomMaxHealth(){
        return customMaxHealth;
    }

    public void setCustomMaxHealth(double customMaxHealth){
        double currentHealthPercentage = this.getHealth() / this.getMaxHealth();
        this.customMaxHealth = customMaxHealth;
        this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(customMaxHealth);
        this.setHealth((float) (customMaxHealth * currentHealthPercentage)); // Adjust current health proportionally
    }

    public int getLevel(){
        return level;
    }

    public void setLevel(int level){
        this.level = level;
    }

    @Override
    public void setCustomName(@Nullable Text name) {
        PlayerData playerData = StateSaverAndLoader.getPlayerState(this.getOwner());
        playerData.buddies.get("skeleton").name = name.getString();
        super.setCustomName(MobBuddyHelper.CustomName(name.getString(), this.getLevel()));
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
    protected boolean isAffectedByDaylight() {
        return false;
    }

    @Override
    protected boolean isDisallowedInPeaceful() {
        return false;
    }
}
