package com.lucker.mobbuddies;

import io.wispforest.lavender.md.features.OwoUIModelFeature;
import io.wispforest.owo.client.OwoClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;

public class MobBuddiesClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		//Register Buddy renderers
		EntityRendererRegistry.register(MobBuddies.ZOMBIE_BUDDY, ZombieEntityRenderer::new);
		EntityRendererRegistry.register(MobBuddies.SKELETON_BUDDY, SkeletonEntityRenderer::new);
		EntityRendererRegistry.register(MobBuddies.CREEPER_BUDDY, CreeperEntityRenderer::new);
		EntityRendererRegistry.register(MobBuddies.SPIDER_BUDDY, SpiderEntityRenderer::new);
		EntityRendererRegistry.register(MobBuddies.ENDERMAN_BUDDY, EndermanEntityRenderer::new);
		EntityRendererRegistry.register(MobBuddies.PIGLIN_BUDDY, BuddyPiglinEntityRenderer::new); // Custom Renderer
		EntityRendererRegistry.register(MobBuddies.GHAST_BUDDY, GhastEntityRenderer::new);
		EntityRendererRegistry.register(MobBuddies.SHULKER_BUDDY, ShulkerEntityRenderer::new);

		//Transparent crop cutout
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ModBlocks.SOULGRAIN_CROP);

		UseItemCallback.EVENT.register((player, world, hand) ->
		{
			// Check if it's the client-side
			if (world.isClient()) {
				// Check if the player is holding a normal book
				if (player.getStackInHand(hand).isOf(Items.BOOK)) {
					openBookUI();
					return TypedActionResult.success(player.getStackInHand(hand));
				}
			}
			return TypedActionResult.pass(player.getStackInHand(hand));
		});
	}

	private void openBookUI() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client != null) {
			client.setScreen(new MyScreen()); // Open UI when player joins
		}
	}
}
