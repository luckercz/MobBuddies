package com.lucker.mobbuddies;

import com.google.common.base.MoreObjects;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CustomShulkerBulletEntity extends ShulkerBulletEntity {
    public CustomShulkerBulletEntity(EntityType<? extends ShulkerBulletEntity> entityType, World world) {
        super(entityType, world);
    }
    public CustomShulkerBulletEntity (World world, LivingEntity owner, Entity target, Direction.Axis axis){
        super(world,owner,target,axis);
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        Entity entity2 = this.getOwner();

        if(entity instanceof PlayerEntity) {
            return;
        }

        ShulkerBuddyEntity shulkerBuddy = entity2 instanceof ShulkerBuddyEntity ? (ShulkerBuddyEntity) entity2 : null;
        DamageSource damageSource = this.getDamageSources().mobProjectile(this, (LivingEntity) entity2);
        boolean bl = entity.damage(damageSource, shulkerBuddy.customProjectileDamage);
        if (bl) {
            World var8 = this.getWorld();
            if (var8 instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld)var8;
                EnchantmentHelper.onTargetDamaged(serverWorld, entity, damageSource);
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity2 = (LivingEntity)entity;
                livingEntity2.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, shulkerBuddy.customLevitationDuration), (Entity) MoreObjects.firstNonNull(entity2, this));
            }
        }
        super.onEntityHit(entityHitResult);
    }
}
