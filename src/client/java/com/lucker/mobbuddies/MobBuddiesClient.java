package com.lucker.mobbuddies;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class MobBuddiesClient implements ClientModInitializer {
	public static final EntityModelLayer MODEL_ZOMBIE_BUDDY_LAYER = EntityModelLayers.ZOMBIE;
	public static final EntityModelLayer MODEL_CUBE_LAYER = new EntityModelLayer(Identifier.of("mob-buddies", "cube"), "main");

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

//		EntityRendererRegistry.INSTANCE.register(MobBuddies.ZOMBIE_BUDDY, (context) ->{
//			return new ZombieBuddyEntityRenderer(context);
//		});;
//
//		EntityModelLayerRegistry.registerModelLayer(MODEL_ZOMBIE_BUDDY_LAYER, ZombieBuddyEntityModel::getTexturedModelData);

		EntityRendererRegistry.register(MobBuddies.CUBE, CubeEntityRenderer::new);
		EntityRendererRegistry.register(MobBuddies.ZOMBIE_BUDDY, ZombieBuddyEntityRenderer::new);

		EntityModelLayerRegistry.registerModelLayer(MODEL_CUBE_LAYER, CubeEntityModel::getTexturedModelData);
	}
}