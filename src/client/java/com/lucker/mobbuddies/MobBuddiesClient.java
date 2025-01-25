package com.lucker.mobbuddies;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.CreeperEntityRenderer;
import net.minecraft.client.render.entity.SkeletonEntityRenderer;
import net.minecraft.client.render.entity.SpiderEntityRenderer;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
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

		EntityModelLayerRegistry.registerModelLayer(MODEL_CUBE_LAYER, CubeEntityModel::getTexturedModelData);
	}
}