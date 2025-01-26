package com.lucker.mobbuddies.mixin;

import com.lucker.mobbuddies.MobBuddies;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExplosionBehavior.class)
public class ExplosionBehaviorMixin {
    @Inject(at = @At("HEAD"), method = "shouldDamage", cancellable = true)
    private void shouldDamage(Explosion explosion, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if(explosion.getEntity().getType() == MobBuddies.CREEPER_BUDDY || explosion.getEntity().getType() == MobBuddies.GHAST_BUDDY){
            if(MobBuddies.MOB_BUDDY_TYPES.contains(entity.getType())){
                cir.setReturnValue(false);
                return;
            }
            else if (entity.isPlayer()) {
                cir.setReturnValue(false);
                return;
            }
        }
    }
}
