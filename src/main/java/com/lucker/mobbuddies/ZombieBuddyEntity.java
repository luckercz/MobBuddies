package com.lucker.mobbuddies;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;


public class ZombieBuddyEntity extends ZombieEntity {

    private UUID pendingOwnerUuid = null;
    private PlayerEntity owner;
    private int level = 1;
    private double customAttackDamage = 5.0; // Default attack damage
    private double customMaxHealth = 20.0; // Default health

    public ZombieBuddyEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);

    }

    public static ZombieBuddyEntity create(World world, PlayerEntity owner, BlockPos pos) {
        MobBuddies.LOGGER.info("Createasdad zombie buddy");
        ZombieBuddyEntity zombieBuddy = new ZombieBuddyEntity(MobBuddies.ZOMBIE_BUDDY, world);
        zombieBuddy.refreshPositionAndAngles(pos, 0.0F, 0.0F);
        zombieBuddy.setOwner(owner);
        zombieBuddy.setCustomName(Text.of("zombuebud"));

        //If unlocked

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
        return !MobBuddies.MOB_BUDDY_TYPES.contains(type);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (owner != null) {
            nbt.putUuid("OwnerUUID", owner.getUuid());
        }

        nbt.putInt("ZombieBuddyLevel", this.level);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid("OwnerUUID")) {
            UUID ownerUuid = nbt.getUuid("OwnerUUID");
            this.setPendingOwner(ownerUuid);
        }

        if(nbt.contains("ZombieBuddyLevel")) {
            this.levelUp(nbt.getInt("ZombieBuddyLevel"));
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
        else if (heldItem.isOf(ModItems.MOB_ENERGY_RAW)) {
            this.levelUp(1);
            player.sendMessage(Text.literal("Your buddy's level is now " + this.getLevel() + "!"), true);
            heldItem.decrement(1);
            return ActionResult.CONSUME;

        }
        else if (heldItem.isOf(Items.NAME_TAG)) {
            this.setCustomName(heldItem.getName());
            player.sendMessage(Text.literal("Your buddy is now named " + heldItem.getName().getString() + "!"), true);
            return ActionResult.CONSUME;
        }

        return super.interactMob(player, hand);
    }

    public void levelUp(int levels){
        this.setCustomAttackDamage(this.getCustomAttackDamage()+ 1.0 * levels);
        this.setCustomMaxHealth(this.getCustomMaxHealth() + 5.0f * levels);
        this.setLevel(this.getLevel() + levels);

        // Play a sound effect
        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    public static DefaultAttributeContainer.Builder createCustomZombieAttributes() {
        return ZombieBuddyEntity.createZombieAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0);
    }

    public double getCustomAttackDamage(){
        return customAttackDamage;
    }

    public void setCustomAttackDamage(double customAttackDamage){
        this.customAttackDamage = customAttackDamage;
        this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(customAttackDamage);
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
