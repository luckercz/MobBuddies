package com.lucker.mobbuddies;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.FluidTags;
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

import java.util.UUID;

public class EndermanBuddyEntity extends EndermanEntity implements  IMobBuddyEntity{
    private UUID pendingOwnerUuid = null;
    private PlayerEntity owner;
    private int level = 1;
    private double customAttackDamage = 5.0; // Default attack damage
    private double customMaxHealth = 40.0; // Default max health
    private float range = 16;

    public EndermanBuddyEntity(EntityType<? extends EndermanEntity> entityType, World world) {
        super(entityType, world);
    }

    public static EndermanBuddyEntity create(World world, PlayerEntity owner, BlockPos pos) {
        MobBuddies.LOGGER.info("Creating EndermanBuddyEntity");

        EndermanBuddyEntity endermanBuddy = (EndermanBuddyEntity) MobBuddyHelper.Create(world, new EndermanBuddyEntity(MobBuddies.ENDERMAN_BUDDY, world), owner, pos);

        PlayerData playerData = StateSaverAndLoader.getPlayerState(owner);
        BuddyData buddyData = playerData.buddies.computeIfAbsent("enderman", k -> new BuddyData());

        MobBuddyHelper.Initialize(endermanBuddy, buddyData.level, buddyData.health, buddyData.name);

        if (world instanceof ServerWorld) {
            ((ServerWorld) world).spawnEntity(endermanBuddy);
        }
        return endermanBuddy;
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
        this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0D, 20.0F, 50.0F)); // Follow owner
        this.goalSelector.add(3, new WanderAroundFarGoal(this, 1.0D)); // Wander
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F)); // Look at players
        this.goalSelector.add(5, new LookAroundGoal(this)); // Randomly look around

        this.targetSelector.add(1, new TeleportTowardsTargetGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, net.minecraft.entity.mob.HostileEntity.class, true)); // Target hostile mobs
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

        nbt.putInt("EndermanBuddyLevel", this.level);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid("OwnerUUID")) {
            UUID ownerUuid = nbt.getUuid("OwnerUUID");
            this.setPendingOwner(ownerUuid);
        }

        if (nbt.contains("EndermanBuddyLevel")) {
            this.levelUp(nbt.getInt("EndermanBuddyLevel"));
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
        ActionResult actionResult = MobBuddyHelper.InteractMob(player, this, hand, ModItems.SHADOWBREAD, ModItems.VOID_ESSENCE);
        if (actionResult == null) {
            return super.interactMob(player, hand);
        } else {
            return actionResult;
        }
    }

    public void levelUp(int levels) {
        this.setCustomAttackDamage(this.getCustomAttackDamage() + 1.0 * levels); // Increase attack damage
        this.setCustomMaxHealth(this.getCustomMaxHealth() + 2.0 * levels); // Increase max health
        this.range = Math.min(this.range + 1.0f * levels, 64.0f);
        this.setLevel(this.getLevel() + levels);
    }
    public static DefaultAttributeContainer.Builder createCustomEndermanAttributes() {
        return EndermanEntity.createEndermanAttributes()
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0);
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
        playerData.buddies.get("enderman").name = name.getString();
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

    boolean teleportTo(Entity entity) {
        Vec3d vec3d = new Vec3d(this.getX() - entity.getX(), this.getBodyY(0.5) - entity.getEyeY(), this.getZ() - entity.getZ());
        vec3d = vec3d.normalize();
        double d = 16.0;
        double e = this.getX() + (this.random.nextDouble() - 0.5) * 8.0 - vec3d.x * 16.0;
        double f = this.getY() + (double)(this.random.nextInt(16) - 8) - vec3d.y * 16.0;
        double g = this.getZ() + (this.random.nextDouble() - 0.5) * 8.0 - vec3d.z * 16.0;
        return this.teleportTo(e, f, g);
    }

    private boolean teleportTo(double x, double y, double z) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);

        while(mutable.getY() > this.getWorld().getBottomY() && !this.getWorld().getBlockState(mutable).blocksMovement()) {
            mutable.move(Direction.DOWN);
        }

        BlockState blockState = this.getWorld().getBlockState(mutable);
        boolean bl = blockState.blocksMovement();
        boolean bl2 = blockState.getFluidState().isIn(FluidTags.WATER);
        if (bl && !bl2) {
            Vec3d vec3d = this.getPos();
            boolean bl3 = this.teleport(x, y, z, true);
            if (bl3) {
                this.getWorld().emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of(this));
                if (!this.isSilent()) {
                    this.getWorld().playSound((PlayerEntity)null, this.prevX, this.prevY, this.prevZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                    this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                }
            }

            return bl3;
        } else {
            return false;
        }
    }

    static class TeleportTowardsTargetGoal extends ActiveTargetGoal<LivingEntity> {
        private final EndermanBuddyEntity enderman;
        @Nullable
        private LivingEntity targetEntity;
        private int teleportCooldown;
        private final TargetPredicate validTargetPredicate;

        public TeleportTowardsTargetGoal(EndermanBuddyEntity enderman) {
            super(enderman, LivingEntity.class, 10, false, false, (entity) ->
                    entity instanceof net.minecraft.entity.mob.HostileEntity // Only target hostile mobs
            );
            this.enderman = enderman;
            this.validTargetPredicate = TargetPredicate.createAttackable()
                    .setBaseMaxDistance(enderman.range);
        }

        @Override
        public boolean canStart() {
            this.targetEntity = this.enderman.getWorld().getClosestEntity(
                    net.minecraft.entity.mob.HostileEntity.class,
                    this.validTargetPredicate,
                    this.enderman,
                    this.enderman.getX(),
                    this.enderman.getY(),
                    this.enderman.getZ(),
                    this.enderman.getBoundingBox().expand(enderman.range)
            );
            return this.targetEntity != null;
        }

        @Override
        public void start() {
            this.teleportCooldown = 0;
            this.enderman.setProvoked(); // Mark as aggressive
            super.start();
        }

        @Override
        public void stop() {
            this.targetEntity = null;
            super.stop();
        }

        @Override
        public boolean shouldContinue() {
            return this.targetEntity != null && this.targetEntity.isAlive() &&
                    this.enderman.squaredDistanceTo(this.targetEntity) <= 256.0 &&
                    super.shouldContinue();
        }

        @Override
        public void tick() {
            if (this.targetEntity != null) {
                // Teleport to the target if far away
                if (this.enderman.squaredDistanceTo(this.targetEntity) > 16.0 &&
                        this.teleportCooldown-- <= 0) {
                    if (this.enderman.teleportTo(this.targetEntity)) {
                        this.teleportCooldown = this.getTickCount(20); // Cooldown before teleporting again
                    }
                }

                // Look at the target and continue attacking
                this.enderman.lookAtEntity(this.targetEntity, 10.0F, 10.0F);
                this.enderman.setTarget(this.targetEntity); // Ensure the target is set
            }

            super.tick();
        }
    }
}