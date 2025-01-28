package com.lucker.mobbuddies;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

public class MobBuddiesClient implements ClientModInitializer {
	public static final EntityModelLayer MODEL_CUBE_LAYER = new EntityModelLayer(Identifier.of("mob-buddies", "cube"), "main");

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		EntityRendererRegistry.register(MobBuddies.CUBE, CubeEntityRenderer::new);

		EntityRendererRegistry.register(MobBuddies.ZOMBIE_BUDDY, ZombieEntityRenderer::new);
		EntityRendererRegistry.register(MobBuddies.SKELETON_BUDDY, SkeletonEntityRenderer::new);
		EntityRendererRegistry.register(MobBuddies.CREEPER_BUDDY, CreeperEntityRenderer::new);
		EntityRendererRegistry.register(MobBuddies.SPIDER_BUDDY, SpiderEntityRenderer::new);
		EntityRendererRegistry.register(MobBuddies.ENDERMAN_BUDDY, EndermanEntityRenderer::new);
		EntityRendererRegistry.register(MobBuddies.PIGLIN_BUDDY, BuddyPiglinEntityRenderer::new); // Custom Renderer
		EntityRendererRegistry.register(MobBuddies.GHAST_BUDDY, GhastEntityRenderer::new);
		EntityRendererRegistry.register(MobBuddies.SHULKER_BUDDY, ShulkerEntityRenderer::new);

		EntityModelLayerRegistry.registerModelLayer(MODEL_CUBE_LAYER, CubeEntityModel::getTexturedModelData);
	}

}
